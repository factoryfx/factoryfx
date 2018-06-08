package de.factoryfx.factory.testfactories;

import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryDictionary;
import de.factoryfx.factory.FastFactoryUtility;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class FastExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,Void,ExampleFactoryA> {
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
