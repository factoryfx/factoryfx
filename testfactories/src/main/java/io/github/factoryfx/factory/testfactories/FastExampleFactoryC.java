package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FastFactoryUtility;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class FastExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,ExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryB referenceAttribute;

    @Override
    public ExampleLiveObjectC createImpl() {
        return new ExampleLiveObjectC();
    }

    static {
        FastFactoryUtility.setup(
                FastExampleFactoryC.class,
                (factory, attributeVisitor) -> {
                    StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
                    FactoryReferenceAttribute<ExampleLiveObjectB,FastExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(FastExampleFactoryB.class).labelText("ExampleA2");

                    attributeVisitor.accept("stringAttribute",FastFactoryUtility.tempAttributeSetup(stringAttribute,(v)->factory.stringAttribute=v,()->factory.stringAttribute));
                    attributeVisitor.accept("referenceAttribute",FastFactoryUtility.tempAttributeSetup(referenceAttribute,(v)->factory.referenceAttribute=v,()->factory.referenceAttribute));
                },
                (factory, factoryVisitor) -> {
                    if (factory.referenceAttribute!=null){
                        factoryVisitor.accept(factory.referenceAttribute);
                    }
                }
        );

    }


}