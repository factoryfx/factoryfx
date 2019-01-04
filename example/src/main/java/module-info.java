module de.factoryfx.example {
    requires de.factoryfx.javafxFactoryEditing;
    requires de.factoryfx.jettyFactory;
    requires de.factoryfx.microserviceRestServer;

    requires ch.qos.logback.classic;
    requires java.desktop;
    requires jackson.annotations;
    requires java.ws.rs;
    requires controlsfx;

    exports de.factoryfx.example.client.view;
    exports de.factoryfx.example.server;
    exports de.factoryfx.example.client;
    exports de.factoryfx.example.server.shop;
    exports de.factoryfx.example.main;
    exports de.factoryfx.example.server.shop.netherlands;
}