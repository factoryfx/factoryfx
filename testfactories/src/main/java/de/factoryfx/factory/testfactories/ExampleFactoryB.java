package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryA.class).labelText("referenceAttribute").nullable();
    public final FactoryReferenceAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(ExampleFactoryC.class).labelText("referenceAttributeC").nullable();

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }

}
