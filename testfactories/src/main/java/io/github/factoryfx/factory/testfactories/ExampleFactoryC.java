package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;

public class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA2");

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

}
