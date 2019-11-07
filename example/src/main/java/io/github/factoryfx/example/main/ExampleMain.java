package io.github.factoryfx.example.main;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.factoryfx.example.client.RichClientBuilder;
import io.github.factoryfx.example.server.ServerBuilder;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.MicroserviceBuilder;
import io.github.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import io.github.factoryfx.factory.exception.ResettingHandler;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;
import org.eclipse.jetty.server.Server;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ExampleMain extends Application {
    //Vm-options:
//--add-exports=javafx.base/com.sun.javafx.runtime=org.controlsfx.controls
//--add-exports=javafx.graphics/com.sun.javafx.css=org.controlsfx.controls
//--add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
//--add-exports=javafx.graphics/com.sun.javafx.scene.traversal=org.controlsfx.controls
//--add-exports=javafx.graphics/com.sun.javafx.scene=org.controlsfx.controls
//--add-exports=javafx.controls/com.sun.javafx.scene.control=org.controlsfx.controls
//--add-exports=javafx.base/com.sun.javafx.collections=org.controlsfx.controls
    @Override
    public void start(Stage primaryStage){

        FactoryTreeBuilder<Server, JettyServerRootFactory> serverBuilder = new ServerBuilder().builder();
        MicroserviceBuilder<Server, JettyServerRootFactory> builder = serverBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<Server, JettyServerRootFactory>()));
        Microservice<Server, JettyServerRootFactory> microservice = builder.build();
        microservice.start();

        RichClientBuilder.createFactoryBuilder(8089,primaryStage, "", "", Locale.ENGLISH,serverBuilder,builder.buildMigrationManager()).
                microservice().build().start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8089/shop"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Platform.runLater(()-> new ExceptionDialog(e).showAndWait());
        });

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Application.launch();
    }






}
