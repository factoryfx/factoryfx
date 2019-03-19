package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

}
