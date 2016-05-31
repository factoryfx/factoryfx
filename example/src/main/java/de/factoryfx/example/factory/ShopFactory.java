package de.factoryfx.example.factory;

import java.util.Optional;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ShopFactory extends FactoryBase<Shop,ShopFactory> {
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata<>("port"));
    public final StringAttribute host = new StringAttribute(new AttributeMetadata<>("host"));
    public final ReferenceListAttribute<ProductFactory> products = new ReferenceListAttribute<>(new AttributeMetadata<>("Products"));

    @Override
    protected Shop createImp(Optional<Shop> previousLiveObject) {
        return new Shop(port.get(),host.get(), products.get().stream().map(productFactory -> productFactory.create()).collect(Collectors.toList()));
    }
}
