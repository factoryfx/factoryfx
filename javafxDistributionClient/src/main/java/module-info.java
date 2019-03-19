module io.github.factoryfx.javafxDistributionClient {
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.ws.rs;
    requires com.fasterxml.jackson.jaxrs.json;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires javafx.controls;
    requires jersey.client;
    requires jersey.media.json.jackson;
    requires jersey.common;

    exports io.github.factoryfx.javafx.distribution.launcher.ui;
}