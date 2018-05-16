package de.factoryfx.example;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.example.client.RichClientBuilder;
import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.ServerBuilder;
import de.factoryfx.example.server.shop.*;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.server.Microservice;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
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

        Microservice<OrderCollector, ServerRootFactory, Void> microservice = new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new AllOrNothingFactoryExceptionHandler())), new InMemoryDataStorage<>(shopFactory));
        microservice.start();

        RichClientRoot richClientFactory = new RichClientBuilder(8089).createFactoryBuilder(primaryStage, "", "", Locale.ENGLISH).buildTree();
        Microservice<Void, RichClientRoot, Void> richClient = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(richClientFactory));
        richClient.start();

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

    public static void main(String[] args) throws URISyntaxException, IOException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Application.launch();
    }






}
