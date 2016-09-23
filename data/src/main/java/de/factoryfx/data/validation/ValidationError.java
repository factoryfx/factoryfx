package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

public class ValidationError {
    public final LanguageText validationDescription;
    public final LanguageText attributeLabel;

    public ValidationError(LanguageText validationDescription, LanguageText attributeLabel) {
        this.validationDescription = validationDescription;
        this.attributeLabel = attributeLabel;
    }
}
