package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute").nullable();
    public final FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA>().labelText("referenceAttribute").nullable();
    public final FactoryAttribute<ExampleFactoryA,ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectC,ExampleFactoryC>().labelText("referenceAttributeC").nullable();

    @Override
    protected ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }

}
