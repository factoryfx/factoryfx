package io.github.factoryfx.docu.configurationdata;

import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientBuilder;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                    .withHost("localhost").withPort(8005)
                    .withResource(ctx.get(SpecificMicroserviceResource.class))
                    .withResource(ctx.get(DatabaseResourceFactory.class))
                );

        builder.addFactory(SpecificMicroserviceResource.class, Scope.SINGLETON);

        builder.addFactory(DatabaseResourceFactory.class, Scope.SINGLETON, ctx->{
            DatabaseResourceFactory databaseResource = new DatabaseResourceFactory();
            databaseResource.url.set("jdbc:postgresql://host/database");
            databaseResource.user.set("user");
            databaseResource.password.set("123");
            return databaseResource;
        });

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().withFilesystemStorage(Paths.get("./docu/src/main/java/io/github/factoryfx/docu/configurationdata/")).build();
        microservice.start();

        {
            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
            microservice.updateCurrentFactory(update);
        }

        {
            MicroserviceRestClient<JettyServerRootFactory> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost", 8005, "", "", JettyServerRootFactory.class);
            DataUpdate<JettyServerRootFactory> update = microserviceRestClient.prepareNewFactory();
            update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
            microservice.updateCurrentFactory(update);
        }


    }

}
