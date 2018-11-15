package de.factoryfx.docu.swagger;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.server.Microservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class Main {

    public static class SwaggerWebserver extends JettyServerFactory<Void, SwaggerWebserver> {
        public final FactoryReferenceAttribute<HelloWorldResource, HelloWorldResourceFactory> resource = new FactoryReferenceAttribute<>(HelloWorldResourceFactory.class);
        @Override
        protected List<Object> getResourcesInstances() {
            return Collections.singletonList(resource.instance());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SwaggerWebserver server = new SwaggerWebserver();

        HttpServerConnectorFactory<Void, SwaggerWebserver> serverConnectorFactory = new HttpServerConnectorFactory<>();
        serverConnectorFactory.host.set("localhost");
        serverConnectorFactory.port.set(8005);
        server.connectors.add(serverConnectorFactory);

        server.resource.set(new HelloWorldResourceFactory());

        Microservice<Void,JettyServer, SwaggerWebserver,Void> microservice
                = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(server));
        microservice.start();


        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8005/HelloWorld/swagger.json")).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
