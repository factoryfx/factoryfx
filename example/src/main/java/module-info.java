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
    requires jersey.media.json.jackson;
    requires jersey.client;
    requires jersey.common;
    requires io.github.factoryfx.domFactoryEditing;

    exports io.github.factoryfx.example.client.view;
    exports io.github.factoryfx.example.server;
    exports io.github.factoryfx.example.client;
    exports io.github.factoryfx.example.server.shop;
    exports io.github.factoryfx.example.main;
    exports io.github.factoryfx.example.server.shop.netherlands;
}