package io.github.factoryfx.dom.rest;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class MicroserviceDomResourceTest {
    public static class JettyServerRootFactory extends JettyServerFactory<JettyServerRootFactory> {

    }


    @Test
    @SuppressWarnings("unchecked")
    void test_createNewFactory_integrationtest() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);



        FactoryTreeBuilder<Server, JettyServerRootFactory, Void> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class, ctx->{
            return new JettyServerBuilder<JettyServerRootFactory>().withHost("localhost").withPort(8015).withResource(ctx.getUnsafe(MicroserviceDomResourceFactory.class)).buildTo(new JettyServerRootFactory());
        });
        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON, context -> {
            MicroserviceDomResourceFactory<JettyServerRootFactory, Void> microserviceDomResourceFactory = new MicroserviceDomResourceFactory<>();
            microserviceDomResourceFactory.factoryTreeBuilderBasedAttributeSetup.set(new FactoryTreeBuilderBasedAttributeSetup<>(builder));
            return microserviceDomResourceFactory;
        });

        Microservice<Server, JettyServerRootFactory, Void> microservice = builder.microservice().build();
        try{
            microservice.start();

            DataUpdate<JettyServerRootFactory> jettyServerRootFactoryDataUpdate = microservice.prepareNewFactory();

            MicroserviceDomResource.AttributeAdressingRequest createRequest = new MicroserviceDomResource.AttributeAdressingRequest();
            createRequest.factoryId=jettyServerRootFactoryDataUpdate.root.getId().toString();
            createRequest.attributeVariableName="handler";
            createRequest.root=jettyServerRootFactoryDataUpdate.root;

            HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8015/microservice/createNewFactory/")).POST(HttpRequest.BodyPublishers.ofString(
                    ObjectMapperBuilder.build().writeValueAsString(createRequest)
            )).header("Content-Type", "application/json").build();
            try {
                System.out.println(httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } finally {
            microservice.stop();
        }


    }
}