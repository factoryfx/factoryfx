package de.factoryfx.factory.testfactories;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FastFactoryUtility;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

import java.util.ArrayList;
import java.util.List;

public class FastExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,Void,FastExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryB referenceAttribute;
    public List<FastExampleFactoryB> referenceListAttribute=new ArrayList<>();

    @Override
    public ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(FastFactoryUtility.instance(referenceAttribute), FastFactoryUtility.instances(referenceListAttribute));
    }

    static {
        FastFactoryUtility.setup(
                FastExampleFactoryA.class,
                (factory, attributeVisitor) -> {
                    StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
                    FactoryReferenceAttribute<ExampleLiveObjectB,FastExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(FastExampleFactoryB.class).labelText("ExampleA2");
                    FactoryReferenceListAttribute<ExampleLiveObjectB,FastExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(FastExampleFactoryB.class).labelText("ExampleA3");

                    attributeVisitor.accept("stringAttribute", FastFactoryUtility.tempAttributeSetup(stringAttribute,(v)->factory.stringAttribute=v,()->factory.stringAttribute));
                    attributeVisitor.accept("referenceAttribute", FastFactoryUtility.tempAttributeSetup(referenceAttribute,(v)->factory.referenceAttribute=v,()->factory.referenceAttribute));
                    attributeVisitor.accept("referenceListAttribute", FastFactoryUtility.tempAttributeSetup(referenceListAttribute,(v)->factory.referenceListAttribute=v,()->factory.referenceListAttribute));
                },
                (factory, dataVisitor) -> {
                    if (factory.referenceAttribute!=null){
                        dataVisitor.accept(factory.referenceAttribute);
                    }
                    factory.referenceListAttribute.forEach(dataVisitor);
                }
        );
    }

    public FastExampleFactoryA(){

    }



}
