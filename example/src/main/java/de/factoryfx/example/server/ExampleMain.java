package de.factoryfx.example.server;

import java.util.Arrays;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.development.WebAppViewer;
import de.factoryfx.development.factory.WebGuiApplication;
import de.factoryfx.example.factory.OrderCollector;
import de.factoryfx.example.factory.OrderCollectorToTables;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.NoUserManagement;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class ExampleMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        new WebAppViewer(primaryStage, () -> {
            ShopFactory shopFactory = new ShopFactory();
            shopFactory.stageTitle.set("vehicle shop");
            {
                ProductFactory productFactory = new ProductFactory();
                productFactory.name.set("Car");
                productFactory.price.set(5);
                shopFactory.products.add(productFactory);
            }
            {
                ProductFactory productFactory = new ProductFactory();
                productFactory.name.set("Bike");
                productFactory.price.set(10);
                shopFactory.products.add(productFactory);
            }

            DefaultApplicationServer<OrderCollector, ShopFactory> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(shopFactory));
            applicationServer.start();

            WebGuiApplication<OrderCollector, ShopFactory> webGuiApplication=new WebGuiApplication<>(
                    applicationServer,
                    Arrays.asList(ShopFactory.class,ProductFactory.class),
                    (root)->new InMemoryFactoryStorage<>(root),
                    new NoUserManagement(),()->new OrderCollector(),new OrderCollectorToTables());
            webGuiApplication.start();

        },"http://localhost:8089/#/login");

    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch();
    }






}
