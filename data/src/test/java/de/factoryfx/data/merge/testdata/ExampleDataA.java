package de.factoryfx.data.merge.testdata;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationResult;

public class ExampleDataA extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    public final DataReferenceAttribute<ExampleDataB> referenceAttribute = new DataReferenceAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final DataReferenceListAttribute<ExampleDataB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");

    static {
//        DataDictionary.getDataDictionary(ExampleDataA.class)
//            .setVisitDataChildren((exampleDataA, dataConsumer) -> {
//                if (exampleDataA.referenceAttribute.get()!=null){
//                    dataConsumer.accept(exampleDataA.referenceAttribute.get());
//                }
//                exampleDataA.referenceListAttribute.forEach(dataConsumer);
//            })
//            .setVisitAttributesFlat((exampleDataA, dataConsumer) -> {
//                dataConsumer.accept("stringAttribute",exampleDataA.stringAttribute);
//                dataConsumer.accept("referenceAttribute",exampleDataA.referenceAttribute);
//                dataConsumer.accept("referenceAttributeC",exampleDataA.referenceListAttribute);
//            })
//            .setNewCopyInstanceSupplier(exampleDataA -> new ExampleDataA());

    }

    public ExampleDataA(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);

        config().addValidation(value -> new ValidationResult(false,new LanguageText()), stringAttribute);
    }

}
