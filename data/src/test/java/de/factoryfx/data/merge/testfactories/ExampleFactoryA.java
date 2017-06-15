package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;

public class ExampleFactoryA extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    public final DataReferenceAttribute<ExampleFactoryB> referenceAttribute = new DataReferenceAttribute<>(ExampleFactoryB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final DataReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleFactoryB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");

    public ExampleFactoryA(){
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
