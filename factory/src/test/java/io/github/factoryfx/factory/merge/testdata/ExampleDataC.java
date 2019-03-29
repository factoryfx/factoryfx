package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;


public class ExampleDataC extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleB1");

    static {
//        DataDictionary.getMetadata(ExampleDataC.class).setVisitAttributesFlat((exampleDataC, dataConsumer) -> {
//            dataConsumer.accept("stringAttribute", exampleDataC.stringAttribute);
//        });
//
//        DataDictionary.getMetadata(ExampleDataC.class).setVisitDataChildren((exampleDataC, dataConsumer) -> {
//            //nothing
//        });
//
//        DataDictionary.getMetadata(ExampleDataC.class).setNewCopyInstanceSupplier(exampleDataC -> new ExampleDataC());
    }
}

