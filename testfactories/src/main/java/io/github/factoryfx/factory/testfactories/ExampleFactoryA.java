package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").nullable();
    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2").nullable();
    public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(ExampleFactoryB.class).labelText("ExampleA3");

    @Override
    public ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }


}
