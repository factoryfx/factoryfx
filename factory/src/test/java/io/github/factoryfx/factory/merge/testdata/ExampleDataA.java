package io.github.factoryfx.factory.merge.testdata;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;


public class ExampleDataA extends FactoryBase<Void,ExampleDataA> {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    public final FactoryAttribute<ExampleDataA,Void,ExampleDataB> referenceAttribute = new FactoryAttribute<ExampleDataA,Void,ExampleDataB>().setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final FactoryListAttribute<ExampleDataA,Void,ExampleDataB> referenceListAttribute = new FactoryListAttribute<ExampleDataA,Void,ExampleDataB>().setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");

    public ExampleDataA(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);

        config().addValidation(value -> new ValidationResult(false,new LanguageText()), stringAttribute);
    }

}
