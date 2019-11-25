package io.github.factoryfx.docu.restserver;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class Main {

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder(
                (jetty,ctx)-> jetty.withHost("localhost").withPort(8005).withResource(ctx.get(WebResourceFactory.class))
        );
        builder.addSingleton(WebResourceFactory.class, ctx->{
            String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
            WebResourceFactory webResourceFactory = new WebResourceFactory();
            webResourceFactory.responseText.set("Resource Factory was created at "+time);
            return webResourceFactory;
        });

        Microservice<Server,JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8005/Resource")).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Server responded: "+response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
