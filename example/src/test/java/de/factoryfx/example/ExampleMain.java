package de.factoryfx.example;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.example.client.RichClientBuilder;
import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.ServerBuilder;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.ResettingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.MicroserviceBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ExampleMain extends Application {

    @Override
    public void start(Stage primaryStage){
        ServerRootFactory shopFactory = getShopFactory();
//        MicroserviceBuilder.buildInMemoryMicroservice(shopFactory).start();
        new Microservice<>(new FactoryManager<>(new ResettingFactoryExceptionHandler()), new InMemoryDataStorage<>(shopFactory)).start();

        RichClientRoot richClientFactory = RichClientBuilder.createFactoryBuilder(8089,primaryStage, "", "", Locale.ENGLISH).buildTree();
        MicroserviceBuilder.buildInMemoryMicroservice(richClientFactory).start();

        Platform.runLater(() -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8089/shop"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ServerRootFactory getShopFactory() {
        return new ServerBuilder().build();
    }

//    private RootFactory getNetherlandsShopFactory() {
//        RootFactory shopFactory = new RootFactory();
//        shopFactory.stageTitle.set("vehicle shop");
//
//        VatRateFactory vatRate =new VatRateFactory();
//        vatRate.rate.set(0.21);
//        {
//            NetherlandsCarProductFactory productFactory = new NetherlandsCarProductFactory();
//            productFactory.name.set("Car");
//            productFactory.price.set(5);
//            productFactory.vatRate.set(vatRate);
//            productFactory.bpmTax.set(.05);
//            shopFactory.products.add(productFactory);
//        }
//        {
//            ProductFactory productFactory = new ProductFactory();
//            productFactory.name.set("Bike");
//            productFactory.price.set(10);
//            productFactory.vatRate.set(vatRate);
//            shopFactory.products.add(productFactory);
//        }
//        return shopFactory;
//    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Platform.runLater(()-> new ExceptionDialog(e).showAndWait());
        });

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Application.launch();
    }






}
