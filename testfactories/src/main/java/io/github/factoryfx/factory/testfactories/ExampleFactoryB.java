package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectA,ExampleFactoryA>().labelText("referenceAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectC,ExampleFactoryC>().labelText("referenceAttributeC").nullable();

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }

}
