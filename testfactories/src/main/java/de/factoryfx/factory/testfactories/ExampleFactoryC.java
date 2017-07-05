package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,Void> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

}
