package de.factoryfx.example.main;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.example.client.RichClientBuilder;
import de.factoryfx.example.server.ServerBuilder;
import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ResettingHandler;
import de.factoryfx.server.Microservice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.glyphfont.GlyphFont;
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

        FactoryTreeBuilder<OrderCollector, Server, ServerRootFactory, Void> serverBuilder = new ServerBuilder().builder();
        MicroserviceBuilder<OrderCollector, Server, ServerRootFactory, Void> builder = serverBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<OrderCollector, Server, ServerRootFactory>())).
                withInMemoryStorage();
        Microservice<OrderCollector, Server, ServerRootFactory, Void> microservice = builder.build();
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
