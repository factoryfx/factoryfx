package de.factoryfx.example.factory;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;
import javafx.stage.Stage;

public class ShopFactory extends FactoryBase<Shop,OrderCollector,ShopFactory> {

    public final FactoryReferenceAttribute<JettyServer, ShopJettyServerFactory> httpServer =
            new FactoryReferenceAttribute<JettyServer, ShopJettyServerFactory>().setupUnsafe(JettyServerFactory.class)
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    public ShopFactory(){
        config().setDisplayTextProvider(()->"Shop");

        configLiveCycle().setCreator(() -> new Shop(stageTitle.get(), products.instances(),new Stage(), new OrderStorage()));
        configLiveCycle().setReCreator((previousLiveObject) -> new Shop(stageTitle.get(), products.instances(),previousLiveObject.getStage(), new OrderStorage()));
        configLiveCycle().setStarter(Shop::start);
        configLiveCycle().setDestroyer(Shop::stop);
        configLiveCycle().setRuntimeQueryExecutor((visitor,currentLiveObject) -> currentLiveObject.accept(visitor));
    }

    public final StringAttribute stageTitle = new StringAttribute().labelText("Stage title");

    public final FactoryReferenceListAttribute<Product,ProductFactory> products = new FactoryReferenceListAttribute<>(ProductFactory.class).labelText("Products");

}
