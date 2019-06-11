package io.github.factoryfx.factory.typescript.generator.testserver;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class TestServerMain {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder< Server,SimpleHttpServer,Void> builder = new FactoryTreeBuilder<>(SimpleHttpServer.class);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<SimpleHttpServer>())
                .withHost("localhost").withPort(8005)
                .withResource(ctx.get(WebResourceFactory.class)).build());

        builder.addFactory(WebResourceFactory.class, Scope.SINGLETON, ctx->{
            String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
            WebResourceFactory webResourceFactory = new WebResourceFactory();
            webResourceFactory.responseText.set("Resource Factory was created at "+time);
            return webResourceFactory;
        });

        Microservice<Server,SimpleHttpServer,Void> microservice = builder.microservice().build();
        microservice.start();

//        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8005/Resource")).GET().build();
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Server responded: "+response.body());
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost:8005/index.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
