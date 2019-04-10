package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FastFactoryUtility;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class FastExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,FastExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryA referenceAttribute;
    public FastExampleFactoryC referenceAttributeC;

    @Override
    public ExampleLiveObjectB createImpl() {
        return new ExampleLiveObjectB(FastFactoryUtility.instance(referenceAttributeC));
    }

    static {
        FastFactoryUtility.setup(
                FastExampleFactoryB.class,
                (factory, attributeVisitor) -> {
                    StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
                    FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectA,FastExampleFactoryA> referenceAttribute = new FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectA,FastExampleFactoryA>().labelText("ExampleB2");
                    FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectC,FastExampleFactoryC> referenceAttributeC = new FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectC,FastExampleFactoryC>().labelText("ExampleC2");

                    attributeVisitor.accept("stringAttribute",FastFactoryUtility.tempAttributeSetup(stringAttribute,(v)->factory.stringAttribute=v,()->factory.stringAttribute));
                    attributeVisitor.accept("referenceAttribute",FastFactoryUtility.tempAttributeSetup(referenceAttribute,(v)->factory.referenceAttribute=v,()->factory.referenceAttribute));
                    attributeVisitor.accept("referenceAttributeC",FastFactoryUtility.tempAttributeSetup(referenceAttributeC,(v)->factory.referenceAttributeC=v,()->factory.referenceAttributeC));
                },
                (factory, factoryVisitor) -> {
                    if (factory.referenceAttribute!=null){
                        factoryVisitor.accept(factory.referenceAttribute);
                    }
                    if (factory.referenceAttributeC!=null){
                        factoryVisitor.accept(factory.referenceAttributeC);
                    }
                }
        );
    }


    public FastExampleFactoryB(){

    }
}
