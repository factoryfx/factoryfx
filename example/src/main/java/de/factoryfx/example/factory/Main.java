package de.factoryfx.example.factory;

import java.util.ArrayList;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.guimodel.GuiModel;
import de.factoryfx.guimodel.View;
import de.factoryfx.richclient.GenericTreeFactoryViewRichClient;
import de.factoryfx.richclient.MainStage;
import de.factoryfx.richclient.framework.view.LoadView;
import de.factoryfx.richclient.framework.view.SaveView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {

        ShopFactory shopFactory = new ShopFactory();
        shopFactory.port.set(123);
        shopFactory.host.set("testhost");
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

        FactoryManager<ShopFactory> factoryManager = new FactoryManager<>();

        factoryManager.start(shopFactory);

        ShopFactory localCopyShopFactory = shopFactory.copy();

        ArrayList<View> views = new ArrayList<>();
        GenericTreeFactoryViewRichClient genericTreeFactoryViewRichClient = new GenericTreeFactoryViewRichClient();
        SaveView<ShopFactory> saveView = new SaveView<>(() -> factoryManager.update(shopFactory, localCopyShopFactory));


        MainStage<ShopFactory> factoryEditor =
                new MainStage<>(
                        new GuiModel<>(shopFactory, views),
                        genericTreeFactoryViewRichClient,
                        new LoadView<>(genericTreeFactoryViewRichClient, () -> localCopyShopFactory),
                        saveView
                );
        factoryEditor.show();


    }

    public static void main(String[] args) {
        Application.launch();
    }

}
