package de.factoryfx.data.validation;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.util.LanguageText;

public class ValidationError {
    public final LanguageText validationDescription;
    public final LanguageText attributeLabel;
    public final Attribute<?> attribute;

    public ValidationError(LanguageText validationDescription, Attribute<?> attribute) {
        this.validationDescription = validationDescription;
        this.attributeLabel = attribute.metadata.labelText;
        this.attribute = attribute;
    }
}
