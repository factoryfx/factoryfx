package de.factoryfx.factory.validation;

import de.factoryfx.factory.util.LanguageText;

public class ValidationError {
    public final LanguageText validationDescription;
    public final LanguageText attributeLabel;

    public ValidationError(LanguageText validationDescription, LanguageText attributeLabel) {
        this.validationDescription = validationDescription;
        this.attributeLabel = attributeLabel;
    }
}
