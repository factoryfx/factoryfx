package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FastFactoryUtility;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;

import java.util.ArrayList;
import java.util.List;

public class FastExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,FastExampleFactoryA> {
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
                    FactoryReferenceAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB>().labelText("ExampleA2");
                    FactoryReferenceListAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB>().labelText("ExampleA3");

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
