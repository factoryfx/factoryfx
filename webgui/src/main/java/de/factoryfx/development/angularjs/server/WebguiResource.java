package de.factoryfx.development.angularjs.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.server.ApplicationServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class WebGuiResource {

    private final ApplicationServer<?,?> applicationServer;
    private final Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider;

    public WebGuiResource(ApplicationServer<?,?> applicationServer, Supplier<List<Class<? extends FactoryBase>>> factoryClassesProvider) {
        this.applicationServer = applicationServer;
        this.factoryClassesProvider = factoryClassesProvider;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("metaData")
    public Map<String,WebGuiEntityMetadata> getMetaData() {
        List<Class<? extends FactoryBase>> classList = factoryClassesProvider.get();

        HashMap<String,WebGuiEntityMetadata> result = new HashMap<>();
        for (Class<? extends FactoryBase> factoryBaseClass: classList){
            WebGuiEntityMetadata webGuiEntityMetadata = new WebGuiEntityMetadata(factoryBaseClass);
            result.put(webGuiEntityMetadata.type, webGuiEntityMetadata);
        }

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("root")
    public WebGuiEntity getRoot() {
        return new WebGuiEntity(applicationServer.getCurrentFactory().root,applicationServer.getCurrentFactory().root);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("factory")
    public WebGuiEntity getFactory(@QueryParam("id")String id) {
        FactoryBase<? extends LiveObject<?>, ?> root = applicationServer.getCurrentFactory().root;

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
        FactoryBase<? extends LiveObject<?>, ?> root = applicationServer.getCurrentFactory().root;
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

    }
}
