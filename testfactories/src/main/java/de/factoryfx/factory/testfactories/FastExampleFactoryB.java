package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FastFactoryUtility;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class FastExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryA> {
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
                    FactoryReferenceAttribute<ExampleLiveObjectA,FastExampleFactoryA> referenceAttribute = new FactoryReferenceAttribute<>(FastExampleFactoryA.class).labelText("ExampleB2");
                    FactoryReferenceAttribute<ExampleLiveObjectC,FastExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(FastExampleFactoryC.class).labelText("ExampleC2");

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
