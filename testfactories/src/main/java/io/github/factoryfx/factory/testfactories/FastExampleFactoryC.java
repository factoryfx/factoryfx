package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.fastfactory.FastValueAttribute;


import java.util.List;

public class FastExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,FastExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryB referenceAttribute;

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

    static {
        FastFactoryUtility.setup(FastExampleFactoryC.class, new FastFactoryUtility<>(() -> List.of(
                new FastValueAttribute<>(()->new StringAttribute().labelText("ExampleA1"), (factory) -> factory.stringAttribute, (factory, value) -> factory.stringAttribute = value,"stringAttribute"),
                new FastFactoryAttribute<>(()->new FactoryAttribute<FastExampleFactoryA, ExampleLiveObjectB, FastExampleFactoryB>().labelText("ExampleA2"), (factory) -> factory.referenceAttribute, (factory, value) -> factory.referenceAttribute = value, FastExampleFactoryB.class,"referenceAttribute")

        )));
    }

}
