package io.github.factoryfx.docu.configurationwebapp;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {


    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                .withHost("localhost").withPort(8005).withResource(ctx.get(MicroserviceDomResourceFactory.class)));
        builder.addSingleton(MicroserviceDomResourceFactory.class);

        builder.microservice().build().start();
        try {
            java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8005/microservice/index.html"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

}
