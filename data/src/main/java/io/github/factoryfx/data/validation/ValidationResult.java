package io.github.factoryfx.data.validation;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.util.LanguageText;

public class ValidationResult {
    private final LanguageText languageText;
    private final boolean error;

    public ValidationResult(boolean error, LanguageText languageText) {
        this.languageText = languageText;
        this.error = error;
    }

    public boolean validationFailed(){
        return error;
    }

    public ValidationError createValidationError(Attribute<?,?> attribute, Data parent, String attributeVariableName){
        return new ValidationError(languageText,attribute,parent,attributeVariableName);
    }


}
