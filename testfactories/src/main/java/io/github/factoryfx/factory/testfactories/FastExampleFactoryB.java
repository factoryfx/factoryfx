package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.fastfactory.FastValueAttribute;

import java.util.List;

public class FastExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,FastExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryA referenceAttribute;
    public FastExampleFactoryC referenceAttributeC;

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(FastFactoryUtility.instance(referenceAttributeC));
    }

    static {

        FastFactoryUtility.setup(FastExampleFactoryB.class,
                new FastFactoryUtility<>(() -> List.of(
                        new FastValueAttribute<>(()->new StringAttribute().labelText("ExampleA1"), (factory) -> factory.stringAttribute, (factory, value) -> factory.stringAttribute = value,"stringAttribute"),
                        new FastFactoryAttribute<>(()->new FactoryAttribute<FastExampleFactoryA, ExampleLiveObjectA, FastExampleFactoryA>().labelText("ExampleA2"), (factory) -> factory.referenceAttribute, (factory, value) -> factory.referenceAttribute = value, FastExampleFactoryA.class,"referenceAttribute"),
                        new FastFactoryAttribute<>(()->new FactoryAttribute<FastExampleFactoryA, ExampleLiveObjectC, FastExampleFactoryC>().labelText("ExampleA3"), (factory) -> factory.referenceAttributeC, (factory, value) -> factory.referenceAttributeC = value, FastExampleFactoryC.class,"referenceAttributeC")
        )));
    }


    public FastExampleFactoryB() {

    }


}


