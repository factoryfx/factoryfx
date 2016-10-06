package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import javafx.stage.Stage;

public class ShopFactory extends FactoryBase<Shop,OrderCollector> {
    public ShopFactory(){
        setDisplayTextProvider(()->"Shop");
    }

    public final StringAttribute stageTitle = new StringAttribute(new AttributeMetadata().labelText("Stage title"));

    public final FactoryReferenceListAttribute<Product,ProductFactory> products = new FactoryReferenceListAttribute<>(ProductFactory.class,new AttributeMetadata().labelText("Products"));

    @Override
    protected Shop createImp(Optional<Shop> previousLiveObject, LifecycleNotifier<OrderCollector> lifecycle) {
        OrderStorage orderStorage = new OrderStorage();
        Stage stage;
        if (previousLiveObject.isPresent()){
            stage=previousLiveObject.get().getStage();
            orderStorage=previousLiveObject.get().getOrderStorage();
        } else {
            stage=new Stage();
        }

        return new Shop(stageTitle.get(), products.instances(),stage, orderStorage,lifecycle);
    }
}
