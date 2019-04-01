package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewReferenceAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataIgnore extends SimpleFactoryBase<Void, ExampleData> {
    public final StringAttribute stringAttribute = new StringAttribute();
    public final ObjectValueAttribute<Object> objectValueAttribute = new ObjectValueAttribute<>();
    public final FactoryViewReferenceAttribute<ExampleData, Void,ExampleData> factoryView = new FactoryViewReferenceAttribute<>(root -> null);
    public final FactoryViewListReferenceAttribute<ExampleData,Void,ExampleData> factoryViewList = new FactoryViewListReferenceAttribute<>(root -> null);

    @Override
    public Void createImpl() {
        return null;
    }
}
