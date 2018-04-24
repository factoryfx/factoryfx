package de.factoryfx.example.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.example.client.RichClientBuilder;
import de.factoryfx.example.client.RichClientRoot;
import de.factoryfx.example.factory.*;
import de.factoryfx.example.factory.netherlands.NetherlandsCarProductFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.rest.MicroserviceResourceFactory;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class ExampleMain extends Application {

    @Override
    public void start(Stage primaryStage){
        ShopFactory shopFactory = getShopFactory();

        Microservice<OrderCollector, ShopFactory, Void> microservice = new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new AllOrNothingFactoryExceptionHandler())), new InMemoryDataStorage<>(shopFactory));
        microservice.start();

        RichClientRoot richClientFactory = new RichClientBuilder(8089).createFactoryBuilder(primaryStage, "", "", Locale.ENGLISH).buildTree();
        Microservice<Void, RichClientRoot, Void> richClient = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<>(richClientFactory));
        richClient.start();
    }

    private ShopFactory getShopFactory() {
        ShopFactory shopFactory = new ShopFactory();
        ShopJettyServerFactory shopJettyServer = new ShopJettyServerFactory();
        MicroserviceResourceFactory<OrderCollector, ShopFactory, Void> resource = new MicroserviceResourceFactory<>();
        shopJettyServer.resource.set(resource);
        HttpServerConnectorFactory<OrderCollector,ShopFactory> connector = new HttpServerConnectorFactory<>();
        connector.host.set("localhost");
        connector.port.set(8089);
        shopJettyServer.connectors.add(connector);
        shopFactory.httpServer.set(shopJettyServer);


        shopFactory.stageTitle.set("vehicle shop");

        VatRateFactory vatRate =new VatRateFactory();
        vatRate.rate.set(0.19);
        {
            ProductFactory carFactory = new ProductFactory();
            carFactory.vatRate.set(vatRate);
            carFactory.name.set("Car");
            carFactory.price.set(5);
            shopFactory.products.add(carFactory);
        }
        {
            ProductFactory bikeFactory = new ProductFactory();
            bikeFactory.vatRate.set(vatRate);
            bikeFactory.name.set("Bike");
            bikeFactory.price.set(10);
            shopFactory.products.add(bikeFactory);
        }

        shopFactory = shopFactory.internal().prepareUsableCopy();
        return shopFactory;
    }

    private ShopFactory getNetherlandsShopFactory() {
        ShopFactory shopFactory = new ShopFactory();
        shopFactory.stageTitle.set("vehicle shop");

        VatRateFactory vatRate =new VatRateFactory();
        vatRate.rate.set(0.21);
        {
            NetherlandsCarProductFactory productFactory = new NetherlandsCarProductFactory();
            productFactory.name.set("Car");
            productFactory.price.set(5);
            productFactory.vatRate.set(vatRate);
            productFactory.bpmTax.set(.05);
            shopFactory.products.add(productFactory);
        }
        {
            ProductFactory productFactory = new ProductFactory();
            productFactory.name.set("Bike");
            productFactory.price.set(10);
            productFactory.vatRate.set(vatRate);
            shopFactory.products.add(productFactory);
        }
        return shopFactory;
    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Application.launch();
    }






}
