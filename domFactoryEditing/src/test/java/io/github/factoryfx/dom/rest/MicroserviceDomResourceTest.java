package io.github.factoryfx.dom.rest;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.builder.JettyServerBuilder;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class MicroserviceDomResourceTest {

    @Test
    @SuppressWarnings("unchecked")
    void test_createNewFactory_integrationtest() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);



        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->
                jetty.withHost("localhost").withPort(8015).withResource(ctx.getUnsafe(MicroserviceDomResourceFactory.class))
        );
        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
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