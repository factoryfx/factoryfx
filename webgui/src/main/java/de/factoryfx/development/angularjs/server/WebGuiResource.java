package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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

import de.factoryfx.factory.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.development.angularjs.model.WebGuiFactory;
import de.factoryfx.development.angularjs.model.WebGuiFactoryMetadata;
import de.factoryfx.development.angularjs.model.WebGuiModel;
import de.factoryfx.development.angularjs.model.WebGuiPossibleEntity;
import de.factoryfx.development.angularjs.model.WebGuiUser;
import de.factoryfx.development.angularjs.model.WebGuiValidationError;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.factory.merge.MergeResultEntry;
import de.factoryfx.guimodel.GuiModel;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.User;
import de.factoryfx.user.UserManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class WebGuiResource<V,T extends FactoryBase<? extends LiveObject<V>, T>> {

    private final ApplicationServer<V,T> applicationServer;
    private final Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider;
    private List<Locale> locales =new ArrayList<>();
    private final GuiModel guimodel;
    private final UserManagement userManagement;

    public WebGuiResource(GuiModel guimodel, ApplicationServer<V,T> applicationServer, Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider, List<Locale> locales, UserManagement userManagement) {
        this.applicationServer = applicationServer;
        this.factoryClassesProvider = factoryClassesProvider;
        this.locales =locales;
        this.guimodel=guimodel;
        this.userManagement=userManagement;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("metaData")
    public Map<String,WebGuiFactoryMetadata> getMetaData() {
        List<Class<? extends FactoryBase>> classList = factoryClassesProvider.get();

        HashMap<String,WebGuiFactoryMetadata> result = new HashMap<>();
        for (Class<? extends FactoryBase> factoryBaseClass: classList){
            WebGuiFactoryMetadata webGuiEntityMetadata = new WebGuiFactoryMetadata(factoryBaseClass,getUserLocale());
            result.put(webGuiEntityMetadata.type, webGuiEntityMetadata);
        }

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("root")
    public WebGuiFactory getRoot() {
        return new WebGuiFactory(getCurrentEditingFactoryRoot().root, getCurrentEditingFactoryRoot().root);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public WebGuiFactory getFactory(@QueryParam("id")String id) {
        FactoryBase<?, ?> root = getCurrentEditingFactoryRoot().root;

        //TODO use map?
        for (FactoryBase<?,?> factory: root.collectChildFactories()){
            if (factory.getId().equals(id)){
                return new WebGuiFactory(factory,root);
            }
        }
        throw new IllegalStateException("cant find id"+id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    @SuppressWarnings("unchecked")
    public void save(WebGuiFactory newFactory) {
        newFactory.factory=newFactory.factory.reconstructMetadataDeepRoot();
        FactoryBase<?, ?> root = getCurrentEditingFactoryRoot().root;
        Map<String,FactoryBase<?,?>>  map = root.collectChildFactoriesMap();
        FactoryBase existing = map.get(newFactory.factory.getId());


        //TODO fix generics,casts
        existing.visitAttributesDualFlat(newFactory.factory, (thisAttribute, copyAttribute) -> {
            Object value = ((Attribute)copyAttribute).get();//The cast is necessary don't trust intellij
            if (value instanceof FactoryBase){
                value=map.get(((FactoryBase)value).getId());
            }
            if (copyAttribute instanceof ReferenceListAttribute){
                final ObservableList<FactoryBase> referenceList = FXCollections.observableArrayList();
                ((ReferenceListAttribute<?>)copyAttribute).get().forEach(factory -> referenceList.add(map.get(factory.getId())));
                value=referenceList;
            }

            ((Attribute)thisAttribute).set(value);
        });

        root.fixDuplicateObjects(id -> Optional.of(map.get(id)));

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

    @Context HttpServletRequest request;

    @SuppressWarnings("unchecked")
    private ApplicationFactoryMetadata<T> getCurrentEditingFactoryRoot(){
        return (ApplicationFactoryMetadata<T>) request.getSession(true).getAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("loadCurrentFactory")
    public Response init(){
        if (request.getSession(true).getAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY)==null){
            request.getSession(true).setAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY,applicationServer.getCurrentFactory());
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
    public WebGuiModel getGuimodel(){
        return new WebGuiModel(guimodel,getUserLocale(),userManagement);
    }


    public static class DeployResponse{
        public MergeDiff mergeDiff;
        public List<WebGuiValidationError> validationErrors=new ArrayList<>();
        public boolean deployed;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deploy")
    public DeployResponse deploy(){
        DeployResponse response=new DeployResponse();


        MergeDiff simMergeDiff= applicationServer.simulateUpdateCurrentFactory(getCurrentEditingFactoryRoot(),getUserLocale());
        if (simMergeDiff.hasNoConflicts()){
            if (userManagement.authorisationRequired()){
                User user = getUser();
                for (MergeResultEntry<?> mergeInfo: simMergeDiff.getMergeInfos()){
                    user.checkPermission(mergeInfo.requiredPermission);
                }
            }
        } else {
            //TODO
        }


        for (FactoryBase<?,?> factoryBase: getCurrentEditingFactoryRoot().root.collectChildFactories()){
            factoryBase.validateFlat().stream().map(validationError -> new WebGuiValidationError(validationError,getUserLocale(),factoryBase)).forEach(w -> response.validationErrors.add(w));
        }

        if (response.validationErrors.isEmpty()){
            //TODO handle conflicts
            response.mergeDiff=applicationServer.updateCurrentFactory(getCurrentEditingFactoryRoot(),getUserLocale());
            if (response.mergeDiff.hasNoConflicts()){
                response.deployed=true;
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

        T root = getCurrentEditingFactoryRoot().root;
        root.collectChildFactoriesMap().get(id).visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?,?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference) {
                        List<? extends FactoryBase<?,?>> objects = reference.possibleValues(root);
                        objects.forEach(new Consumer<FactoryBase<?,?>>() {
                            @Override
                            public void accept(FactoryBase<?,?> factoryBase) {
                                result.add(new WebGuiPossibleEntity(factoryBase));
                            }
                        });
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
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
    public WebGuiFactory addFactory(@QueryParam("id")String id, @QueryParam("attributeName")String attributeName){

        T root = getCurrentEditingFactoryRoot().root;
        FactoryBase<?, ?> factoryBase = root.collectChildFactoriesMap().get(id);
        factoryBase.visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeVariableName.equals(attributeName)){

                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?,?> value) {
                        //nothing
                    }

                    @Override
                    public void reference(ReferenceAttribute<?> reference) {
                        reference.addNewFactory(root);
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
                        referenceList.addNewFactory(root);
                    }
                });

            }
        });
        return new WebGuiFactory(factoryBase,root);
    }

}
