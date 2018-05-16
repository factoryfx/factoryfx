module de.factoryfx.example {
    requires de.factoryfx.factory;
    requires de.factoryfx.javafxDataEditing;
    requires de.factoryfx.javafxFactoryEditing;
    requires javafx.graphics;
    requires de.factoryfx.data;
    requires de.factoryfx.jettyFactory;
    requires javafx.controls;
    requires de.factoryfx.microserviceRestServer;
    requires slf4j.api;
    requires logback.classic;
    requires de.factoryfx.microserviceRestClient;
    requires java.ws.rs;
    requires java.desktop;
    requires java.activation;
    requires com.fasterxml.jackson.databind;
    requires de.factoryfx.microserviceRestCommon;
    requires jackson.annotations;
    requires controlsfx;

    exports de.factoryfx.example.client.view;
    exports de.factoryfx.example.server;
    exports de.factoryfx.example.client;
    exports de.factoryfx.example.server.shop;

}