package de.factoryfx.data.merge.testdata;

import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleDataB extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final DataReferenceAttribute<ExampleDataA> referenceAttribute = new DataReferenceAttribute<>(ExampleDataA.class).labelText("ExampleB2");
    public final DataReferenceAttribute<ExampleDataC> referenceAttributeC = new DataReferenceAttribute<>(ExampleDataC.class).labelText("ExampleC2");

    static {
//        DataDictionary.getDataDictionary(ExampleDataB.class).setVisitDataChildren((exampleDataB, dataConsumer) -> {
//            if (exampleDataB.referenceAttribute.get()!=null){
//                dataConsumer.accept(exampleDataB.referenceAttribute.get());
//            }
//            if (exampleDataB.referenceAttributeC.get()!=null){
//                dataConsumer.accept(exampleDataB.referenceAttributeC.get());
//            }
//        });
//        DataDictionary.getDataDictionary(ExampleDataB.class).setVisitAttributesFlat((exampleDataB, dataConsumer) -> {
//            dataConsumer.accept("stringAttribute",exampleDataB.stringAttribute);
//            dataConsumer.accept("referenceAttribute",exampleDataB.referenceAttribute);
//            dataConsumer.accept("referenceAttributeC",exampleDataB.referenceAttributeC);
//        });
//
//        DataDictionary.getDataDictionary(ExampleDataB.class).setNewCopyInstanceSupplier(exampleDataA -> new ExampleDataB());
    }

}
