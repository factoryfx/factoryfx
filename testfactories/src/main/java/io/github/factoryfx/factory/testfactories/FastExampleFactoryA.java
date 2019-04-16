package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FastFactoryUtility;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

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
                    FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB> referenceAttribute = new FactoryAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB>().labelText("ExampleA2");
                    FactoryListAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB> referenceListAttribute = new FactoryListAttribute<FastExampleFactoryA,ExampleLiveObjectB,FastExampleFactoryB>().labelText("ExampleA3");

                    attributeVisitor.accept("stringAttribute", FastFactoryUtility.tempAttributeSetup(stringAttribute,(v)->factory.stringAttribute=v,()->factory.stringAttribute));
                    attributeVisitor.accept("referenceAttribute", FastFactoryUtility.tempAttributeSetup(referenceAttribute,(v)->factory.referenceAttribute=v,()->factory.referenceAttribute,FastExampleFactoryB.class));
                    attributeVisitor.accept("referenceListAttribute", FastFactoryUtility.tempAttributeSetup(referenceListAttribute,(v)->factory.referenceListAttribute=v,()->factory.referenceListAttribute,FastExampleFactoryB.class));
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
