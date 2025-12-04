module io.github.factoryfx.javafxDistributionClient {
    requires javafx.fxml;
    requires javafx.graphics;
    requires jakarta.ws.rs;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires javafx.controls;
    requires org.glassfish.jersey.media.json.jackson;
    requires org.glassfish.jersey.core.client;
    requires org.glassfish.jersey.core.common;


    exports io.github.factoryfx.javafx.distribution.launcher.ui;
}