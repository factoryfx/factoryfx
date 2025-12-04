open module io.github.factoryfx.example {
    requires io.github.factoryfx.javafxFactoryEditing;
    requires io.github.factoryfx.jettyFactory;
    requires io.github.factoryfx.microserviceRestServer;

    requires ch.qos.logback.classic;
    requires java.desktop;
    requires jakarta.ws.rs;
    requires org.eclipse.jetty.server;

    requires javafx.base;
    requires javafx.graphics;
    requires io.github.factoryfx.domFactoryEditing;
    requires org.glassfish.jersey.core.common;
    requires org.glassfish.jersey.core.client;
    requires org.glassfish.jersey.media.json.jackson;

    exports io.github.factoryfx.example.client.view;
    exports io.github.factoryfx.example.server;
    exports io.github.factoryfx.example.client;
    exports io.github.factoryfx.example.server.shop;
    exports io.github.factoryfx.example.main;
    exports io.github.factoryfx.example.server.shop.netherlands;
}