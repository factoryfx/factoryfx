package de.factoryfx.server.angularjs.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeResultEntry;
import de.factoryfx.data.merge.MergeResultEntryInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.angularjs.model.FactoryTypeInfoWrapper;
import de.factoryfx.server.angularjs.model.WebGuiFactoryMetadata;
import de.factoryfx.server.angularjs.model.WebGuiMergeDiff;
import de.factoryfx.server.angularjs.model.WebGuiPossibleEntity;
import de.factoryfx.server.angularjs.model.WebGuiUser;
import de.factoryfx.server.angularjs.model.WebGuiValidationError;
import de.factoryfx.server.angularjs.model.table.WebGuiTable;
import de.factoryfx.server.angularjs.model.view.GuiView;
import de.factoryfx.server.angularjs.model.view.ViewHeader;
import de.factoryfx.server.angularjs.model.view.WebGuiView;
import de.factoryfx.user.AuthorizedUser;
import de.factoryfx.user.UserManagement;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Path("/") /** path defined in {@link de.factoryfx.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class RestResource<L,V,T extends FactoryBase<L,V>> {

    private final ApplicationServer<L,V,T> applicationServer;
    private final List<Class<? extends FactoryBase>> appFactoryClasses;
    private final List<Locale> locales;
    private final Layout webGuiLayout;
    private final UserManagement userManagement;
    private final Function<V,List<WebGuiTable>> dashboardTablesProvider;
    private final Supplier<V> emptyVisitorCreator;
    private final List<GuiView<?>> views;
    private final SessionStorage sessionStorage;

    /**
     * @param applicationServer
     * @param appFactoryClasses the factory class from application
     * @param locales
     * @param userManagement
     * @param sessionStorage
     */
    public RestResource(Layout layout, ApplicationServer<L,V,T> applicationServer, List<Class<? extends FactoryBase>> appFactoryClasses, List<Locale> locales, UserManagement userManagement, Supplier<V> emptyVisitorCreator, Function<V, List<WebGuiTable>> dashboardTablesProvider, List<GuiView<?>> views, SessionStorage sessionStorage) {
        this.applicationServer = applicationServer;
        this.appFactoryClasses = appFactoryClasses;
        this.locales = locales;
        this.webGuiLayout = layout;
        this.userManagement = userManagement;
        this.dashboardTablesProvider= dashboardTablesProvider;
        this.emptyVisitorCreator = emptyVisitorCreator;
        this.views=views;
        this.sessionStorage = sessionStorage;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("metaData")
    public Map<String,WebGuiFactoryMetadata> getMetaData() {
        HashMap<String,WebGuiFactoryMetadata> result = new HashMap<>();
        for (Class<? extends FactoryBase> factoryBaseClass: appFactoryClasses){
            WebGuiFactoryMetadata webGuiEntityMetadata = new WebGuiFactoryMetadata(factoryBaseClass,getUserLocale());
            result.put(webGuiEntityMetadata.type, webGuiEntityMetadata);
        }

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("root")
    public de.factoryfx.server.angularjs.model.WebGuiFactory getRoot() {
        return new de.factoryfx.server.angularjs.model.WebGuiFactory(getCurrentEditingFactory().root, getCurrentEditingFactory().root);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public de.factoryfx.server.angularjs.model.WebGuiFactory getFactory(@QueryParam("id")String id) {
        FactoryBase<?,?> root = getCurrentEditingFactory().root;

        //TODO use map?
        for (Data factory: root.internal().collectChildrenDeep()){
            if (factory.getId().equals(id)){
                return new de.factoryfx.server.angularjs.model.WebGuiFactory((FactoryBase<?,?>)factory,root);
            }
        }
        throw new IllegalStateException("cant find id"+id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    @SuppressWarnings("unchecked")
    public StageResponse save(FactoryTypeInfoWrapper newFactoryParam) {
        FactoryBase<?,?> newFactory=newFactoryParam.factory.internal().reconstructMetadataDeepRoot();
        FactoryBase<?,?> root = getCurrentEditingFactory().root;
        Map<Object,Data>  map = root.internal().collectChildFactoriesMap();
        Data existing = map.get(newFactory.getId());

        Function<Data,Data> existingOrNew= factoryBase -> {
            Data result = map.get(factoryBase.getId());
            if (factoryBase!=null && result==null){//new added nested factory
                result=factoryBase;
            }
            return result;
        };


        //TODO fix generics,casts
        //copy FactoryUpdate into existing
        existing.internal().visitAttributesDualFlat(newFactory, (thisAttribute, copyAttribute) -> {
            Object value = ((Attribute)copyAttribute).get();//The cast is necessary don't trust intellij
            if (value instanceof FactoryBase){
                value=existingOrNew.apply((Data)value);
            }
            if (copyAttribute instanceof ReferenceListAttribute){
                final ObservableList<Data> referenceList = FXCollections.observableArrayList();
                ((ReferenceListAttribute)copyAttribute).get().forEach(factory -> referenceList.add(existingOrNew.apply((Data)factory)));
                value=referenceList;
            }

            ((Attribute)thisAttribute).set(value);
        });

        Map<Object,Data>  map2 = root.internal().collectChildFactoriesMap();
        root.internal().fixDuplicateObjects(id -> Optional.of(map2.get(id)));

        return createStageResponse();
    }

    public static class LoginResponse{
        public boolean successfully;
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public LoginResponse login(WebGuiUser user) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.successfully =false;
        Optional<AuthorizedUser> authenticatedUser = userManagement.authenticate(user.user, user.password);
        if (authenticatedUser.isPresent()){
            loginResponse.successfully =true;
            sessionStorage.loginUser(request,authenticatedUser.get());


        }
        return loginResponse;
    }

    @Context
    HttpServletRequest request;

    @SuppressWarnings("unchecked")
    private FactoryAndStorageMetadata<T> getCurrentEditingFactory(){
        return (FactoryAndStorageMetadata<T>)sessionStorage.getCurrentEditingFactory(request);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("loadCurrentFactory")
    public Response init(){
        if (!sessionStorage.hasCurrentEditingFactory(request)){
            FactoryAndStorageMetadata<T> prepareNewFactory = applicationServer.getPrepareNewFactory();
            prepareNewFactory.metadata.user=getUser().user;
            sessionStorage.setCurrentEditingFactory(request,prepareNewFactory);
        }
        return Response.ok().entity("ok").build();
    }

    private Locale getUserLocale(){
        if (request==null){//hack for testability
            return Locale.ENGLISH;
        }
        if (request.getSession(false)==null){
            return request.getLocale();
        } else  {
            return getUser().locale;
        }
    }

    private AuthorizedUser getUser(){
        return sessionStorage.getUser(request);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("locales")
    public List<String> getLocales(){
        ArrayList<String> result = new ArrayList<>();
        for (Locale locale: locales){
            result.add(locale.toString());
        }
        return result;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("guimodel")
    public Layout getGuimodel(){
        return webGuiLayout;
    }


    public static class StageResponse {
        public WebGuiMergeDiff mergeDiff;
        @JsonIgnore
        public MergeDiff mergeDiffExt;
        public List<WebGuiValidationError> validationErrors=new ArrayList<>();
        public boolean deployed;
    }

    private StageResponse createStageResponse(){
        StageResponse response=new StageResponse();
        FactoryAndStorageMetadata<T> currentEditingFactoryRoot = getCurrentEditingFactory();

        response.mergeDiffExt=applicationServer.simulateUpdateCurrentFactory(currentEditingFactoryRoot);
        response.mergeDiff=new WebGuiMergeDiff(response.mergeDiffExt,getUserLocale());

        for (Data factoryBase: currentEditingFactoryRoot.root.internal().collectChildrenDeep()){
            factoryBase.internal().validateFlat().stream().map(validationError -> new WebGuiValidationError(validationError,getUserLocale(),factoryBase)).forEach(w -> response.validationErrors.add(w));
        }

        return response;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deployReset")
    public StageResponse deployReset(){
        sessionStorage.setCurrentEditingFactory(request,applicationServer.getPrepareNewFactory());
        return createStageResponse();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deploy")
    public StageResponse deploy(){
        StageResponse response=createStageResponse();


        if (response.mergeDiffExt.hasNoConflicts()){
            if (userManagement.authorisationRequired()){
                AuthorizedUser user = getUser();
                for (MergeResultEntry mergeInfo: response.mergeDiffExt.getMergeInfos()){
                    user.checkPermission(mergeInfo.requiredPermission);
                }
            }
        } else {
            //TODO
        }


        if (response.validationErrors.isEmpty()){
            //TODO handle conflicts
            response.mergeDiffExt=applicationServer.updateCurrentFactory(getCurrentEditingFactory());
            response.mergeDiff=new WebGuiMergeDiff(response.mergeDiffExt,getUserLocale());
            if (response.mergeDiffExt.hasNoConflicts()){
                response.deployed=true;
                sessionStorage.setCurrentEditingFactory(request,applicationServer.getPrepareNewFactory());
            }
        }


        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("possibleValues")
    @SuppressWarnings("unchecked")
    public List<WebGuiPossibleEntity> possibleValues(@QueryParam("id")String id, @QueryParam("attributeName")String attributeName){
        List<WebGuiPossibleEntity> result = new ArrayList<>() ;

        T root = getCurrentEditingFactory().root;
        root.internal().prepareUsage();

        root.internal().collectChildFactoriesMap().get(id).internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference) {
                        List<? extends Data> objects = reference.possibleValues();
                        objects.forEach(data -> result.add(new WebGuiPossibleEntity(data)));
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
                        List<? extends Data> objects = referenceList.possibleValues();
                        objects.forEach(data -> result.add(new WebGuiPossibleEntity(data)));
                    }
                });

            }
        });
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("newEntry")
    @SuppressWarnings("unchecked")
    public de.factoryfx.server.angularjs.model.WebGuiFactory addFactory(@QueryParam("id")String id, @QueryParam("attributeName")String attributeName){

        T root = getCurrentEditingFactory().root;
        root.internal().prepareUsage();

        Data factoryBase = root.internal().collectChildFactoriesMap().get(id);
        factoryBase.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference) {
                        reference.addNewFactory();
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
                        referenceList.addNewFactory();
                    }
                });

            }
        });
        return new de.factoryfx.server.angularjs.model.WebGuiFactory((FactoryBase<?,?>) factoryBase,root);
    }

    public static class DashboardResponse{
        public List<WebGuiTable> tables =new ArrayList<>();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("dashboard")
    public DashboardResponse dashboard(){
        V visitor = emptyVisitorCreator.get();
        applicationServer.query(visitor);


        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.tables=dashboardTablesProvider.apply(visitor);
        return dashboardResponse;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("history") //detail request for history
    public Collection<StoredFactoryMetadata> history(){
        List<StoredFactoryMetadata> historyFactoryList = new ArrayList<>(applicationServer.getHistoryFactoryList());
        Collections.sort(historyFactoryList, (o1, o2) -> Objects.compare(o1.creationTime, o2.creationTime, (o11, o21) -> o21.compareTo(o11)));
        return historyFactoryList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("views")
    public List<ViewHeader> getViews() {
        ArrayList<ViewHeader> viewHeaders = new ArrayList<>();
        for (GuiView<?> view: views){
            viewHeaders.add(view.createHeader(getUserLocale()));
        }
        return viewHeaders;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("view")
    public WebGuiView getView(@QueryParam("id")String id) {
        for (GuiView<?> view: views){
            if (view.id.equals(id)){
                return view.createWebGuiView(getCurrentEditingFactory().root,getUserLocale());
            }
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    public MergeDiff getDiff(@QueryParam("id")String id) {
        applicationServer.getHistoryFactory(id);
        for(StoredFactoryMetadata storedFactoryMetadata: applicationServer.getHistoryFactoryList()){
            if (storedFactoryMetadata.id.equals(id)){
                return applicationServer.getDiff(storedFactoryMetadata);
            }
        }

        return null;
    }

    public static class DiffDetailResponse{
        public String text;//text/plain dont work well with angulrajs

        public DiffDetailResponse(String text) {
            this.text = text;
        }
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("diffdetail")
    public DiffDetailResponse getDiffDetail(MergeResultEntryInfo info) {
        Patch<String> patch = DiffUtils.diff(
                convertToList(info.previousValueDisplayText),
                convertToList(info.newValueValueDisplayText)
        );
        String originalText=info.previousValueDisplayText;


        int previousOriginalPosition=0;
        StringBuilder diffString = new StringBuilder();
        String  lastOriginalStringDelta="";

        for (Delta<String> delta: patch.getDeltas()) {
            String originalStringDelta = delta.getOriginal().getLines().stream().collect(Collectors.joining());
            String revisitedStringDelta = delta.getRevised().getLines().stream().collect(Collectors.joining());
            final String unchanged=originalText.substring(previousOriginalPosition,delta.getOriginal().getPosition()).replace(lastOriginalStringDelta,"");

            diffString.append("<span class=\"diffUnchanged\">");
            diffString.append(unchanged);
            diffString.append("</span>");

            diffString.append("<span class=\"diffOld\">");
            diffString.append(originalStringDelta);
            diffString.append("</span>");

            diffString.append("<span class=\"diffNew\">");
            diffString.append(revisitedStringDelta);
            diffString.append("</span>");

            previousOriginalPosition=delta.getOriginal().getPosition();
            lastOriginalStringDelta=originalStringDelta;
        }
        final String unchanged = originalText.substring(previousOriginalPosition, originalText.length()).replace(lastOriginalStringDelta,"");
        diffString.append(unchanged);

        return new DiffDetailResponse(diffString.toString());
    }

    private List<String> convertToList(String value){
        //Arrays.asList(value.split(""))  slower
        ArrayList<String> result = new ArrayList<>(value.length());
        for (int i = 0;i < value.length(); i++){
            result.add(String.valueOf(value.charAt(i)));
        }
        return result;
    }
}
