package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).labelText("referenceAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(ExampleFactoryC.class).labelText("referenceAttributeC").nullable();

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }

}
