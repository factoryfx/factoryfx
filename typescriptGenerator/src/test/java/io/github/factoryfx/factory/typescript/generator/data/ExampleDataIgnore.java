package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.attribute.DataViewListReferenceAttribute;
import io.github.factoryfx.data.attribute.DataViewReferenceAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import io.github.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.data.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;


public class ExampleDataIgnore extends SimpleFactoryBase<Void, ExampleFactoryA> {
    public final StringAttribute stringAttribute = new StringAttribute();
    public final ObjectValueAttribute<Object> objectValueAttribute = new ObjectValueAttribute<>();
    public final DataViewReferenceAttribute<ExampleFactoryA,ExampleFactoryA> dataView = new DataViewReferenceAttribute<>(root -> null);
    public final DataViewListReferenceAttribute<ExampleFactoryA,ExampleFactoryA> dataViewList = new DataViewListReferenceAttribute<>(root -> null);
    public final FactoryViewReferenceAttribute<ExampleFactoryA, ExampleLiveObjectA,ExampleFactoryA> factoryView = new FactoryViewReferenceAttribute<>(root -> null);
    public final FactoryViewListReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> factoryViewList = new FactoryViewListReferenceAttribute<>(root -> null);

    @Override
    public Void createImpl() {
        return null;
    }
}
