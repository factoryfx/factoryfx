package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

public class ExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").nullable();
    public final FactoryAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA2").nullable();
    public final FactoryListAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryListAttribute<ExampleFactoryA,ExampleLiveObjectB,ExampleFactoryB>().labelText("ExampleA3");

    @Override
    protected ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }


}
