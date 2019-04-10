package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataIgnore extends SimpleFactoryBase<Void, ExampleData> {
    public final StringAttribute stringAttribute = new StringAttribute();
    public final ObjectValueAttribute<Object> objectValueAttribute = new ObjectValueAttribute<>();
    public final FactoryViewAttribute<ExampleData, Void,ExampleData> factoryView = new FactoryViewAttribute<>(root -> null);
    public final FactoryViewListAttribute<ExampleData,Void,ExampleData> factoryViewList = new FactoryViewListAttribute<>(root -> null);

    @Override
    public Void createImpl() {
        return null;
    }
}
