package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;


public class ExampleDataA extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    public final FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataB> referenceAttribute = new FactoryReferenceAttribute<ExampleDataA,Void,ExampleDataB>().setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final FactoryReferenceListAttribute<ExampleDataA,Void,ExampleDataB> referenceListAttribute = new FactoryReferenceListAttribute<ExampleDataA,Void,ExampleDataB>().setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");

    static {
//        DataDictionary.getMetadata(ExampleDataA.class)
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
