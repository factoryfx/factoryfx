package io.github.factoryfx.example.server.factoryupdatelogleak;

import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * Simplified version of {@link io.github.factoryfx.microservice.rest.MicroserviceResource}. Triggers an update by flipping the {@link NestedObjectFactory#booleanAttribute} value
 * */

@Path("/microservice")
public class SimplifiedMicroserviceResource {
    private final Microservice<?, JettyServerRootFactory> microservice;

    public SimplifiedMicroserviceResource(Microservice<?, JettyServerRootFactory> microservice) {
        this.microservice = microservice;
    }

    @GET
    @Path("triggerUpdate")
    @Produces("application/json")
    public FactoryUpdateLog<JettyServerRootFactory> triggerUpdate() {
        DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();

        NestedObjectFactory nestedObjectFactory = update.root.utility().getFactoryTreeBuilder().buildSubTree(NestedObjectFactory.class);

        nestedObjectFactory.booleanAttribute.set(!nestedObjectFactory.booleanAttribute.get());

        return microservice.updateCurrentFactory(update);
    }
}
