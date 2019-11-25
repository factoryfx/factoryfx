package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute").nullable();
    public final FactoryAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute = new FactoryAttribute<ExampleLiveObjectA,ExampleFactoryA>().labelText("referenceAttribute").nullable();
    public final FactoryAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryAttribute<ExampleLiveObjectC,ExampleFactoryC>().labelText("referenceAttributeC").nullable();

    @Override
    protected ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(referenceAttributeC.instance());
    }
}
