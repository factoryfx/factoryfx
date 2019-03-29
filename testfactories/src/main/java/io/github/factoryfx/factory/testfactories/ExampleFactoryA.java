package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;

public class ExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").nullable();
    public final FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA2").nullable();
    public final FactoryReferenceListAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA3");

    @Override
    public ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }


}
