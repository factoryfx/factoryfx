package de.factoryfx.docu.swagger;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.jetty.*;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static class SwaggerWebserver extends SimpleFactoryBase<Server, Void, SwaggerWebserver> {
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server, JettyServerFactory<Void, SwaggerWebserver>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder<SwaggerWebserver> builder = new FactoryTreeBuilder<>(SwaggerWebserver.class);
        builder.addFactory(SwaggerWebserver.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<Void,SwaggerWebserver>())
                .withHost("localhost").widthPort(8005)
                .withResource(ctx.get(HelloWorldResourceFactory.class)).build());
        builder.addFactory(HelloWorldResourceFactory.class, Scope.SINGLETON);

        Microservice<Void, Server, SwaggerWebserver,Void> microservice
                = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(builder.buildTree()));
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
