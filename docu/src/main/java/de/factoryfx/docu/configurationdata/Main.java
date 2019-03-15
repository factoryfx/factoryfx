package de.factoryfx.docu.configurationdata;

import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.jetty.JettyServerBuilder;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder< Server,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new RootFactory())
                .withHost("localhost").withPort(8005)
                .withResource(ctx.get(SpecificMicroserviceResource.class))
                .withResource(ctx.get(DatabaseResourceFactory.class)).build());
        builder.addFactory(SpecificMicroserviceResource.class, Scope.SINGLETON);

        builder.addFactory(DatabaseResourceFactory.class, Scope.SINGLETON, ctx->{
            DatabaseResourceFactory databaseResource = new DatabaseResourceFactory();
            databaseResource.url.set("jdbc:postgresql://host/database");
            databaseResource.user.set("user");
            databaseResource.password.set("123");
            return databaseResource;
        });

        Microservice<Server,RootFactory,Void> microservice = builder.microservice().withFilesystemStorage(Paths.get("./docu/src/main/java/de/factoryfx/docu/configurationdata/")).build();
        microservice.start();

        {
            DataUpdate<RootFactory> update = microservice.prepareNewFactory();
            update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
            microservice.updateCurrentFactory(update);
        }

        {
            MicroserviceRestClient<RootFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost", 8005, "", "", RootFactory.class);
            DataUpdate<RootFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
            microservice.updateCurrentFactory(update);
        }


    }

}
