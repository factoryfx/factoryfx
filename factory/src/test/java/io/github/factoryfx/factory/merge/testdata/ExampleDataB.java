package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataB extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute = new FactoryAttribute<ExampleDataA,Void,ExampleDataA>().labelText("ExampleB2");
    public final FactoryAttribute<ExampleDataA,Void,ExampleDataC> referenceAttributeC = new FactoryAttribute<ExampleDataA,Void,ExampleDataC>().labelText("ExampleC2");

    static {
//        DataDictionary.getMetadata(ExampleDataB.class).setVisitDataChildren((exampleDataB, dataConsumer) -> {
//            if (exampleDataB.referenceAttribute.get()!=null){
//                dataConsumer.accept(exampleDataB.referenceAttribute.get());
//            }
//            if (exampleDataB.referenceAttributeC.get()!=null){
//                dataConsumer.accept(exampleDataB.referenceAttributeC.get());
//            }
//        });
//        DataDictionary.getMetadata(ExampleDataB.class).setVisitAttributesFlat((exampleDataB, dataConsumer) -> {
//            dataConsumer.accept("stringAttribute",exampleDataB.stringAttribute);
//            dataConsumer.accept("referenceAttribute",exampleDataB.referenceAttribute);
//            dataConsumer.accept("referenceAttributeC",exampleDataB.referenceAttributeC);
//        });
//
//        DataDictionary.getMetadata(ExampleDataB.class).setNewCopyInstanceSupplier(exampleDataA -> new ExampleDataB());
    }

}
