package de.factoryfx.development.angularjs.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.server.ApplicationServer;

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
    public WebGuiEntity<?> getRoot() {
        List<Class<? extends FactoryBase>> classList = factoryClassesProvider.get();

        List<WebGuiEntityMetadata> result = new ArrayList<>();
        for (Class<? extends FactoryBase> factoryBaseClass: classList){
            result.add(new WebGuiEntityMetadata(factoryBaseClass));
        }

        return new WebGuiEntity<>(applicationServer.getCurrentFactory().root);
    }
}
