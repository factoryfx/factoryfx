package de.factoryfx.example.factory;

import java.util.Optional;
import java.util.stream.Collectors;

import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;
import javafx.stage.Stage;

public class ShopFactory extends FactoryBase<Shop,ShopFactory> {
    public final StringAttribute stageTitle = AttributeBuilder.string().labelText("stageTitle").build();
    public final ReferenceListAttribute<ProductFactory> products = AttributeBuilder.<ProductFactory>referenceList().labelText("Products").build();

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

        return new Shop(stageTitle.get(), products.get().stream().map(productFactory -> productFactory.create()).collect(Collectors.toList()),stage, orderStorage);
    }
}
