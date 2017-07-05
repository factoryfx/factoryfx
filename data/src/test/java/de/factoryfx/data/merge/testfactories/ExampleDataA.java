package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;

public class ExampleDataA extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    public final DataReferenceAttribute<ExampleDataB> referenceAttribute = new DataReferenceAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final DataReferenceListAttribute<ExampleDataB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");

    public ExampleDataA(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);

        config().addValidation(new Validation<Object>() {
            @Override
            public LanguageText getValidationDescription() {
                return null;
            }

            @Override
            public boolean validate(Object value) {
                return false;
            }
        }, stringAttribute);
    }

}