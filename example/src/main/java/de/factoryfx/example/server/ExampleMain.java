package de.factoryfx.example.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.ByteStreams;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.example.factory.OrderCollector;
import de.factoryfx.example.factory.OrderCollectorToTables;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.example.factory.Shop;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.example.factory.VatRateFactory;
import de.factoryfx.example.factory.netherlands.NetherlandsCarProductFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.util.ClasspathBasedFactoryProvider;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.angularjs.WebGuiApplicationCreator;
import de.factoryfx.server.angularjs.factory.server.HttpServer;
import de.factoryfx.server.angularjs.factory.server.HttpServerFactory;
import de.factoryfx.server.angularjs.model.view.GuiView;
import de.factoryfx.server.angularjs.model.view.WebGuiFactoryHeader;
import de.factoryfx.testutils.SingleProcessInstanceUtil;
import de.factoryfx.testutils.WebAppViewer;
import de.factoryfx.user.nop.NoUserManagement;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class ExampleMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        new WebAppViewer(primaryStage, () -> {
            ShopFactory shopFactory = getNetherlandsShopFactory();

            ApplicationServer<OrderCollector, Shop, ShopFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new LoggingFactoryExceptionHandler<>(new AllOrNothingFactoryExceptionHandler<>())), new InMemoryDataStorage<>(shopFactory));
            applicationServer.start();

            WebGuiApplicationCreator<OrderCollector, Shop, ShopFactory,Void> webGuiApplicationCreator=new WebGuiApplicationCreator<>(
                    applicationServer,
                    new ClasspathBasedFactoryProvider().get(ShopFactory.class),
                    new NoUserManagement(),
                    OrderCollector::new,new OrderCollectorToTables(),
                    Collections.singletonList(new GuiView<>("sgjhfgdsj", new LanguageText().en("Products"), shopFactory1 -> shopFactory1.products.stream().map(WebGuiFactoryHeader::new).collect(Collectors.toList())))
            );
            HttpServerFactory<OrderCollector, Shop, ShopFactory,Void> defaultFactory = webGuiApplicationCreator.createDefaultFactory();
            try (InputStream inputStream = WebGuiApplicationCreator.class.getResourceAsStream("/logo/logo.png")) {
                defaultFactory.webGuiResource.get().layout.get().logoSmall.set(ByteStreams.toByteArray(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            ApplicationServer<Void, HttpServer, HttpServerFactory<OrderCollector, Shop, ShopFactory,Void>,Void> shopApplication = webGuiApplicationCreator.createApplication(new InMemoryDataStorage<>(defaultFactory));
            shopApplication.start();

        },"http://localhost:8089/#/login");

    }

    private ShopFactory getShopFactory() {
        ShopFactory shopFactory = new ShopFactory();
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

        SingleProcessInstanceUtil.enforceSingleProcessInstance(37453);
        Application.launch();
    }






}
