package de.factoryfx.example.server;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.development.WebAppViewer;
import de.factoryfx.development.angularjs.server.WebGuiResource;
import de.factoryfx.development.angularjs.server.WebGuiServer;
import de.factoryfx.development.angularjs.server.resourcehandler.ClasspathMinifingFileContentProvider;
import de.factoryfx.development.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.guimodel.GuiModel;
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

            GuiModel guiModel = new GuiModel();
            guiModel.title.en("Vehicle shop admin interface");
//            try(InputStream inputStream= WebGuiTest.class.getResourceAsStream("/logo/logoLarge.png")){
//                guiModel.logoLarge= ByteStreams.toByteArray(inputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            try(InputStream inputStream= WebGuiTest.class.getResourceAsStream("/logo/logoSmall.png")){
//                guiModel.logoSmall= ByteStreams.toByteArray(inputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            DefaultApplicationServer<OrderCollector, ShopFactory> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), new InMemoryFactoryStorage<>(shopFactory));
            applicationServer.start();
            new WebGuiServer(8089, "localhost", new WebGuiResource<>(guiModel,applicationServer, () -> Arrays.asList(ShopFactory.class,ProductFactory.class),Arrays.asList(Locale.ENGLISH),new NoUserManagement()),
                    new ConfigurableResourceHandler(new ClasspathMinifingFileContentProvider(), () -> UUID.randomUUID().toString())).start();
        },"http://localhost:8089/#/login");




//        ShopFactory shopFactory = new ShopFactory();
//        shopFactory.stageTitle.set("Simple Example");
//        {
//            ProductFactory productFactory = new ProductFactory();
//            productFactory.name.set("Product1");
//            productFactory.price.set(5);
//            shopFactory.products.add(productFactory);
//        }
//        {
//            ProductFactory productFactory = new ProductFactory();
//            productFactory.name.set("Product2");
//            productFactory.price.set(10);
//            shopFactory.products.add(productFactory);
//        }
//
//        DefaultApplicationServer<OrderCollector,ShopFactory> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(),new InMemoryFactoryStorage<>(shopFactory));
//        applicationServer.start();
//
//        ApplicationFactoryMetadata<ShopFactory> localCopyShopFactory=applicationServer.getCurrentFactory();
//
////        FactoryTreeEditor<ShopFactory> factoryTreeEditor = new FactoryTreeEditor<>();
////        SaveView<ShopFactory> saveView = new SaveView<>(() -> applicationServer.updateCurrentFactory(localCopyShopFactory, Locale.ENGLISH));
//
//        GuiModel guiModel = new GuiModel();
//
//        TableColumn<OrderStorage.Order> productColumn = new TableColumn<>("Product", (order)-> order.productName);
//        TableColumn<OrderStorage.Order> customerColumn = new TableColumn<>("Customer", (order)-> order.customerName);
//        guiModel.runtimeQueryViews.add(new RuntimeQueryView<>("Orders", s -> {
//            OrderCollector visitor = new OrderCollector();
//            applicationServer.query(visitor);
//            return visitor.getOrders();
//        },new Table<>(Arrays.asList(productColumn,customerColumn))));

//        MainStage<ShopFactory> factoryEditor =
//                new MainStage<>(guiModel, factoryTreeEditor,
//                        new LoadView<>(factoryTreeEditor, () -> localCopyShopFactory.root),
//                        saveView
//                );
//        factoryEditor.show();


    }

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch();
    }






}
