package de.factoryfx.adminui.angularjs.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

import de.factoryfx.adminui.angularjs.model.FactoryTypeInfoWrapper;
import de.factoryfx.adminui.angularjs.model.WebGuiFactoryMetadata;
import de.factoryfx.adminui.angularjs.model.WebGuiPossibleEntity;
import de.factoryfx.adminui.angularjs.model.WebGuiUser;
import de.factoryfx.adminui.angularjs.model.WebGuiValidationError;
import de.factoryfx.adminui.angularjs.model.table.WebGuiTable;
import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.adminui.angularjs.model.view.ViewHeader;
import de.factoryfx.adminui.angularjs.model.view.WebGuiView;
import de.factoryfx.adminui.angularjs.server.AuthorizationRequestFilter;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.merge.FactoryMerger;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.factory.merge.MergeResultEntry;
import de.factoryfx.factory.util.VoidLiveObject;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class WebGuiResource<V,T extends FactoryBase<? extends LiveObject<V>, T>>  extends VoidLiveObject {

    private final ApplicationServer<V,T> applicationServer;
    private final List<Class<? extends FactoryBase>> appFactoryClasses;
    private final List<Locale> locales;
    private final WebGuiLayout webGuiLayout;
    private final UserManagement userManagement;
    private final Function<V,List<WebGuiTable>> dashboardTablesProvider;
    private final Supplier<V> emptyVisitorCreator;
    private final List<GuiView<?>> views;

    /**
     *
     * @param applicationServer
     * @param appFactoryClasses the factory class from application
     * @param locales
     * @param userManagement
     */
    public WebGuiResource(
            WebGuiLayout webGuiLayout,
            ApplicationServer<V,T> applicationServer,
            List<Class<? extends FactoryBase>> appFactoryClasses,
            List<Locale> locales,
            UserManagement userManagement,
            Supplier<V> emptyVisitorCreator,
            Function<V,List<WebGuiTable>> dashboardTablesProvider,
            List<GuiView<?>> views) {
        this.applicationServer = applicationServer;
        this.appFactoryClasses = appFactoryClasses;
        this.locales = locales;
        this.webGuiLayout = webGuiLayout;
        this.userManagement = userManagement;
        this.dashboardTablesProvider= dashboardTablesProvider;
        this.emptyVisitorCreator = emptyVisitorCreator;
        this.views=views;
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
    public de.factoryfx.adminui.angularjs.model.WebGuiFactory getRoot() {
        return new de.factoryfx.adminui.angularjs.model.WebGuiFactory(getCurrentEditingFactory().root, getCurrentEditingFactory().root);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public de.factoryfx.adminui.angularjs.model.WebGuiFactory getFactory(@QueryParam("id")String id) {
        FactoryBase<?, ?> root = getCurrentEditingFactory().root;

        //TODO use map?
        for (FactoryBase<?,?> factory: root.collectChildFactories()){
            if (factory.getId().equals(id)){
                return new de.factoryfx.adminui.angularjs.model.WebGuiFactory(factory,root);
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
        FactoryBase<?,?> newFactory=newFactoryParam.factory.reconstructMetadataDeepRoot();
        FactoryBase<?, ?> root = getCurrentEditingFactory().root;
        Map<String,FactoryBase<?,?>>  map = root.collectChildFactoriesMap();
        FactoryBase existing = map.get(newFactory.getId());


        //TODO fix generics,casts
        existing.visitAttributesDualFlat(newFactory, (thisAttribute, copyAttribute) -> {
            Object value = ((Attribute)copyAttribute).get();//The cast is necessary don't trust intellij
            if (value instanceof FactoryBase){
                value=map.get(((FactoryBase)value).getId());
            }
            if (copyAttribute instanceof ReferenceListAttribute){
                final ObservableList<FactoryBase> referenceList = FXCollections.observableArrayList();
                ((ReferenceListAttribute<?,?>)copyAttribute).get().forEach(factory -> referenceList.add(map.get(factory.getId())));
                value=referenceList;
            }

            ((Attribute)thisAttribute).set(value);
        });

        root.fixDuplicateObjects(id -> Optional.of(map.get(id)));

        return createStageResponse();
    }

    private static final String CURRENT_EDITING_FACTORY_SESSION_KEY = "CurrentEditingFactory";
    private static final String USER = "USER_";

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
        Optional<User> authenticatedUser = userManagement.authenticate(user.user, user.password);
        if (authenticatedUser.isPresent()){
            loginResponse.successfully =true;
            request.getSession(true).setAttribute(AuthorizationRequestFilter.LOGIN_SESSION_KEY,true);
            request.getSession(true).setAttribute(USER,authenticatedUser.get());
        }
        return loginResponse;
    }

    @Context
    HttpServletRequest request;

    @SuppressWarnings("unchecked")
    private FactoryAndStorageMetadata<T> getCurrentEditingFactory(){
        return (FactoryAndStorageMetadata<T>) request.getSession(true).getAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("loadCurrentFactory")
    public Response init(){
        if (request.getSession(true).getAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY)==null){
            FactoryAndStorageMetadata<T> prepareNewFactory = applicationServer.getPrepareNewFactory();
            prepareNewFactory.metadata.user=getUser().user;
            request.getSession(true).setAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY, prepareNewFactory);
        }
        return Response.ok().entity("ok").build();
    }

    private Locale getUserLocale(){
        if (request.getSession(false)==null){
            return request.getLocale();
        } else  {
            return getUser().locale;
        }
    }

    private User getUser(){
        return (User) request.getSession(true).getAttribute(USER);
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
    public WebGuiLayout getGuimodel(){
        return webGuiLayout;
    }


    public static class StageResponse {
        public MergeDiff mergeDiff;
        public List<WebGuiValidationError> validationErrors=new ArrayList<>();
        public boolean deployed;
    }

    private StageResponse createStageResponse(){
        StageResponse response=new StageResponse();
        FactoryAndStorageMetadata<T> currentEditingFactoryRoot = getCurrentEditingFactory();
        response.mergeDiff= applicationServer.simulateUpdateCurrentFactory(currentEditingFactoryRoot.root, currentEditingFactoryRoot.metadata.baseVersionId,getUserLocale());


        for (FactoryBase<?,?> factoryBase: currentEditingFactoryRoot.root.collectChildFactories()){
            factoryBase.validateFlat().stream().map(validationError -> new WebGuiValidationError(validationError,getUserLocale(),factoryBase)).forEach(w -> response.validationErrors.add(w));
        }

        return response;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deployReset")
    public StageResponse deployReset(){
        request.getSession(true).setAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY,applicationServer.getPrepareNewFactory());
        return createStageResponse();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deploy")
    public StageResponse deploy(){
        StageResponse response=createStageResponse();


        if (response.mergeDiff.hasNoConflicts()){
            if (userManagement.authorisationRequired()){
                User user = getUser();
                for (MergeResultEntry<?> mergeInfo: response.mergeDiff.getMergeInfos()){
                    user.checkPermission(mergeInfo.requiredPermission);
                }
            }
        } else {
            //TODO
        }


        if (response.validationErrors.isEmpty()){
            //TODO handle conflicts
            response.mergeDiff=applicationServer.updateCurrentFactory(getCurrentEditingFactory(),getUserLocale());
            if (response.mergeDiff.hasNoConflicts()){
                response.deployed=true;

                request.getSession(true).setAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY,applicationServer.getPrepareNewFactory());
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
        root.collectChildFactoriesMap().get(id).visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?,?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?,?> reference) {
                        List<? extends FactoryBase<?,?>> objects = reference.possibleValues(root);
                        objects.forEach(new Consumer<FactoryBase<?,?>>() {
                            @Override
                            public void accept(FactoryBase<?,?> factoryBase) {
                                result.add(new WebGuiPossibleEntity(factoryBase));
                            }
                        });
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?,?> referenceList) {
                        List<? extends FactoryBase<?,?>> objects = referenceList.possibleValues(root);
                        objects.forEach(new Consumer<FactoryBase<?,?>>() {
                            @Override
                            public void accept(FactoryBase<?,?> factoryBase) {
                                result.add(new WebGuiPossibleEntity(factoryBase));
                            }
                        });
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
    public de.factoryfx.adminui.angularjs.model.WebGuiFactory addFactory(@QueryParam("id")String id, @QueryParam("attributeName")String attributeName){

        T root = getCurrentEditingFactory().root;
        FactoryBase<?, ?> factoryBase = root.collectChildFactoriesMap().get(id);
        factoryBase.visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?,?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?,?> reference) {
                        reference.addNewFactory(root);
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?,?> referenceList) {
                        referenceList.addNewFactory(root);
                    }
                });

            }
        });
        return new de.factoryfx.adminui.angularjs.model.WebGuiFactory(factoryBase,root);
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
    @Path("history")
    public Collection<StoredFactoryMetadata> history(){
        return applicationServer.getHistoryFactoryList();
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
                T historyFactory = applicationServer.getHistoryFactory(id);
                T historyFactoryPrevious = applicationServer.getHistoryFactory(storedFactoryMetadata.baseVersionId);
                return new FactoryMerger(historyFactory,historyFactory,historyFactoryPrevious).createMergeResult();
            }
        }

        return null;
    }
}
