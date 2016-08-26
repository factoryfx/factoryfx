package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;
import javafx.stage.Stage;

public class ShopFactory extends FactoryBase<Shop,ShopFactory> {
    {
        setDisplayTextProvider(factoryBase -> "Shop");
    }

    public final StringAttribute stageTitle = new StringAttribute(new AttributeMetadata().labelText("Stage title"));

    public final ReferenceListAttribute<Product,ProductFactory> products = new ReferenceListAttribute<Product,ProductFactory>(ProductFactory.class,new AttributeMetadata().labelText("Products"));

    @Override
    protected Shop createImp(Optional<Shop> previousLiveObject) {
        OrderStorage orderStorage = new OrderStorage();
        Stage stage;
        if (previousLiveObject.isPresent()){
            stage=previousLiveObject.get().getStage();
            orderStorage=previousLiveObject.get().getOrderStorage();
        } else {
            stage=new Stage();
        }

        return new Shop(stageTitle.get(), products.instances(),stage, orderStorage);
    }
}
