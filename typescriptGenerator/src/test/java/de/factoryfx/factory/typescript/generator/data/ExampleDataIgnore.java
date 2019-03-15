package de.factoryfx.factory.typescript.generator.data;

import de.factoryfx.data.attribute.DataViewListReferenceAttribute;
import de.factoryfx.data.attribute.DataViewReferenceAttribute;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;


public class ExampleDataIgnore extends SimpleFactoryBase<Void, ExampleFactoryA> {
    public final StringAttribute stringAttribute = new StringAttribute();
    public final ObjectValueAttribute<Object> objectValueAttribute = new ObjectValueAttribute<>();
    public final DataViewReferenceAttribute<ExampleFactoryA,ExampleFactoryA> dataView = new DataViewReferenceAttribute<>(root -> null);
    public final DataViewListReferenceAttribute<ExampleFactoryA,ExampleFactoryA> dataViewList = new DataViewListReferenceAttribute<>(root -> null);
    public final FactoryViewReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> factoryView = new FactoryViewReferenceAttribute<>(root -> null);
    public final FactoryViewListReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> factoryViewList = new FactoryViewListReferenceAttribute<>(root -> null);

    @Override
    public Void createImpl() {
        return null;
    }
}
