package de.factoryfx.example.factory;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import javafx.stage.Stage;

public class ShopFactory extends FactoryBase<Shop,OrderCollector> {

    public ShopFactory(){
        config().setDisplayTextProvider(()->"Shop");
    }

    public final StringAttribute stageTitle = new StringAttribute(new AttributeMetadata().labelText("Stage title"));

    public final FactoryReferenceListAttribute<Product,ProductFactory> products = new FactoryReferenceListAttribute<>(ProductFactory.class,new AttributeMetadata().labelText("Products"));


    @Override
    public LiveCycleController<Shop, OrderCollector> createLifecycleController() {
        return new LiveCycleController<Shop, OrderCollector>() {
            @Override
            public Shop create() {
                return new Shop(stageTitle.get(), products.instances(),new Stage(), new OrderStorage());
            }

            @Override
            public  Shop reCreate(Shop previousLiveObject) {
                return new Shop(stageTitle.get(), products.instances(),previousLiveObject.getStage(), new OrderStorage());
            }

            @Override
            public void start(Shop newLiveObject) {
                newLiveObject.start();
            }

            @Override
            public void destroy(Shop previousLiveObject) {
                previousLiveObject.stop();
            };

            @Override
            public void runtimeQuery(OrderCollector visitor, Shop currentLiveObject) {
                currentLiveObject.accept(visitor);
            };
        };
    }
}
