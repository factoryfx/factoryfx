package io.github.factoryfx.docu.swagger;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static class SwaggerWebserver extends SimpleFactoryBase<Server, SwaggerWebserver> {
        public final FactoryAttribute<SwaggerWebserver,Server, JettyServerFactory<SwaggerWebserver>> server = new FactoryAttribute<>();

        @Override
        public Server createImpl() {
            return server.instance();
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder< Server, SwaggerWebserver,Void> builder = new FactoryTreeBuilder<>(SwaggerWebserver.class);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<SwaggerWebserver>())
                .withHost("localhost").withPort(8005)
                .withResource(ctx.get(HelloWorldResourceFactory.class)).build());
        builder.addFactory(HelloWorldResourceFactory.class, Scope.SINGLETON);

        Microservice<Server, SwaggerWebserver,Void> microservice
                = builder.microservice().withInMemoryStorage().build();
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
