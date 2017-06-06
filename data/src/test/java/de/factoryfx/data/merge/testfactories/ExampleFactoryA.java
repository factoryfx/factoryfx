package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;

public class ExampleFactoryA extends Data {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
    public final DataReferenceAttribute<ExampleFactoryB> referenceAttribute = new DataReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2")).setCopySemantic(CopySemantic.SELF);
    public final DataReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA3")).setCopySemantic(CopySemantic.SELF);

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
