package de.factoryfx.example.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.ByteStreams;
import de.factoryfx.adminui.InMemoryFactoryStorage;
import de.factoryfx.adminui.SinglePrecessInstanceUtil;
import de.factoryfx.adminui.WebAppViewer;
import de.factoryfx.adminui.angularjs.factory.WebGuiApplication;
import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.adminui.angularjs.model.view.WebGuiFactoryHeader;
import de.factoryfx.adminui.angularjs.util.ClasspathBasedFactoryProvider;
import de.factoryfx.example.factory.OrderCollector;
import de.factoryfx.example.factory.OrderCollectorToTables;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.example.factory.VatRateFactory;
import de.factoryfx.example.factory.netherlands.CarProductFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.util.LanguageText;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.NoUserManagement;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class ExampleMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        new WebAppViewer(primaryStage, () -> {
            ShopFactory shopFactory = getNetherlandsShopFactory();

            DefaultApplicationServer<OrderCollector, ShopFactory> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(shopFactory));
            applicationServer.start();

            WebGuiApplication<OrderCollector, ShopFactory> webGuiApplication=new WebGuiApplication<>(
                    applicationServer,
                    new ClasspathBasedFactoryProvider().get(ShopFactory.class), InMemoryFactoryStorage::new,
                    new NoUserManagement(),
                    (config)->{
                        try (InputStream inputStream = WebGuiApplication.class.getResourceAsStream("/logo/logo.png")) {
                            config.webGuiResource.get().layout.get().logoSmall.set(ByteStreams.toByteArray(inputStream));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    OrderCollector::new,new OrderCollectorToTables(),
                    Arrays.asList(new GuiView<>("sgjhfgdsj", new LanguageText().en("Products"), shopFactory1 -> shopFactory1.products.stream().map(WebGuiFactoryHeader::new).collect(Collectors.toList())))
            );
            webGuiApplication.start();

        },"http://localhost:8089/#/login");

    }

    private ShopFactory getShopFactory() {
        ShopFactory shopFactory = new ShopFactory();
        shopFactory.stageTitle.set("vehicle shop");

        VatRateFactory vatRate =new VatRateFactory();
        vatRate.rate.set(0.19);
        {
            ProductFactory productFactory = new ProductFactory();
            productFactory.name.set("Car");
            productFactory.price.set(5);
            productFactory.vatRate.set(vatRate);
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

    private ShopFactory getNetherlandsShopFactory() {
        ShopFactory shopFactory = new ShopFactory();
        shopFactory.stageTitle.set("vehicle shop");

        VatRateFactory vatRate =new VatRateFactory();
        vatRate.rate.set(0.21);
        {
            CarProductFactory productFactory = new CarProductFactory();
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

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch();
    }






}
