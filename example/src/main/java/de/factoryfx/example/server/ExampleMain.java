package de.factoryfx.example.server;

import java.util.Arrays;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.development.InMemoryFactoryStorage;
import de.factoryfx.development.SinglePrecessInstanceUtil;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.guimodel.GuiModel;
import de.factoryfx.guimodel.RuntimeQueryView;
import de.factoryfx.guimodel.Table;
import de.factoryfx.guimodel.TableColumn;
import de.factoryfx.richclient.GenericTreeFactoryViewRichClient;
import de.factoryfx.richclient.MainStage;
import de.factoryfx.richclient.framework.view.LoadView;
import de.factoryfx.richclient.framework.view.SaveView;
import de.factoryfx.server.DefaultApplicationServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class ExampleMain extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {

        ShopFactory shopFactory = new ShopFactory();
        shopFactory.stageTitle.set("Simple Example");
        {
            ProductFactory productFactory = new ProductFactory();
            productFactory.name.set("Product1");
            productFactory.price.set(5);
            shopFactory.products.add(productFactory);
        }
        {
            ProductFactory productFactory = new ProductFactory();
            productFactory.name.set("Product2");
            productFactory.price.set(10);
            shopFactory.products.add(productFactory);
        }

        DefaultApplicationServer<OrderCollector,ShopFactory> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(),new InMemoryFactoryStorage<>(shopFactory));
        applicationServer.start();

        ApplicationFactoryMetadata<ShopFactory> localCopyShopFactory=applicationServer.getCurrentFactory();

        GenericTreeFactoryViewRichClient genericTreeFactoryViewRichClient = new GenericTreeFactoryViewRichClient();
        SaveView<ShopFactory> saveView = new SaveView<>(() -> applicationServer.updateCurrentFactory(localCopyShopFactory));

        GuiModel guiModel = new GuiModel();

        TableColumn<OrderStorage.Order> productColumn = new TableColumn<>("Product", (order)-> order.productName);
        TableColumn<OrderStorage.Order> customerColumn = new TableColumn<>("Customer", (order)-> order.customerName);
        guiModel.runtimeQueryViews.add(new RuntimeQueryView<>("Orders", s -> {
            OrderCollector visitor = new OrderCollector();
            applicationServer.query(visitor);
            return visitor.getOrders();
        },new Table<>(Arrays.asList(productColumn,customerColumn))));

        MainStage<ShopFactory> factoryEditor =
                new MainStage<>(guiModel,
                        genericTreeFactoryViewRichClient,
                        new LoadView<>(genericTreeFactoryViewRichClient, () -> localCopyShopFactory.root),
                        saveView
                );
        factoryEditor.show();


    }

    public static void main(String[] args) {
        SinglePrecessInstanceUtil.enforceSingleProzessInstance(37453);
        Application.launch();
    }

}
