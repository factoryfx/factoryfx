package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataB extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataA> referenceAttribute = new FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataA>().labelText("ExampleB2");
    public final FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataC> referenceAttributeC = new FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataC>().labelText("ExampleC2");

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
