package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class WebGuiResource {

    private final ApplicationServer<?,?> applicationServer;
    private final Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider;
    private List<Locale> locales =new ArrayList<>();

    public WebGuiResource(ApplicationServer<?,?> applicationServer, Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider, List<Locale> locales ) {
        this.applicationServer = applicationServer;
        this.factoryClassesProvider = factoryClassesProvider;
        this.locales =locales;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("metaData")
    public Map<String,WebGuiEntityMetadata> getMetaData() {
        List<Class<? extends FactoryBase>> classList = factoryClassesProvider.get();

        HashMap<String,WebGuiEntityMetadata> result = new HashMap<>();
        for (Class<? extends FactoryBase> factoryBaseClass: classList){
            WebGuiEntityMetadata webGuiEntityMetadata = new WebGuiEntityMetadata(factoryBaseClass,getUserLocale());
            result.put(webGuiEntityMetadata.type, webGuiEntityMetadata);
        }

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("root")
    public WebGuiEntity getRoot() {
        return new WebGuiEntity(getCurrentEditingFactoryRoot(), getCurrentEditingFactoryRoot());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public WebGuiEntity getFactory(@QueryParam("id")String id) {
        FactoryBase<?, ?> root = getCurrentEditingFactoryRoot();

        //TODO use map?
        for (FactoryBase<?,?> factory: root.collectModelEntities()){
            if (factory.getId().equals(id)){
                return new WebGuiEntity(factory,root);
            }
        }
        throw new IllegalStateException("cant find id"+id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public void save(WebGuiEntity newFactory) {
        newFactory.factory=newFactory.factory.reconstructMetadataDeepRoot();
        FactoryBase<?, ?> root = getCurrentEditingFactoryRoot();
        Map<String,FactoryBase<?,?>>  map = root.collectModelEntitiesMap();
        FactoryBase existing = map.get(newFactory.factory.getId());


        //TODO fix generics,casts
        existing.visitAttributesDualFlat(newFactory.factory, (thisAttribute, copyAttribute) -> {
            Object value = ((Attribute)copyAttribute).get();
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

    public static final String CURRENT_EDITING_FACTORY_SESSION_KEY = "CurrentEditingFactory";
    public static final String USER_LOCALE = "USER_LOCALE";

    public static class LoginResponse{
        public boolean successfully;
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public LoginResponse login(User user) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.successfully =true;

        request.getSession(true).setAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY,applicationServer.getCurrentFactory().root);
        request.getSession(true).setAttribute(USER_LOCALE,Locale.forLanguageTag(user.locale));

        return loginResponse;
    }

    @Context HttpServletRequest request;

    public FactoryBase<?,?> getCurrentEditingFactoryRoot(){
        return (FactoryBase<?, ?>) request.getSession(true).getAttribute(CURRENT_EDITING_FACTORY_SESSION_KEY);

    }

    public Locale getUserLocale(){
        return (Locale) request.getSession(true).getAttribute(USER_LOCALE);
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


}
