package io.github.factoryfx.docu.configurationwebapp;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        FactoryTreeBuilder< Server, SimpleHttpServer,Void> builder = new FactoryTreeBuilder<>(SimpleHttpServer.class);
        new FactoryTreeBuilder<>(SimpleHttpServer.class).addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<SimpleHttpServer>()
                .withHost("localhost").withPort(8005)
                .withResource(ctx.get(MicroserviceDomResourceFactory.class)).build());

        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON);

        builder.microservice().build().start();

        try {
            java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8005/microservice/index.html"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

}
