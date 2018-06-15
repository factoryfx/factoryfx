package de.factoryfx.data.merge.testdata;

import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleDataC extends Data {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleB1");

    static {
//        DataDictionary.getDataDictionary(ExampleDataC.class).setVisitAttributesFlat((exampleDataC, dataConsumer) -> {
//            dataConsumer.accept("stringAttribute", exampleDataC.stringAttribute);
//        });
//
//        DataDictionary.getDataDictionary(ExampleDataC.class).setVisitDataChildren((exampleDataC, dataConsumer) -> {
//            //nothing
//        });
//
//        DataDictionary.getDataDictionary(ExampleDataC.class).setNewCopyInstanceSupplier(exampleDataC -> new ExampleDataC());
    }
}

