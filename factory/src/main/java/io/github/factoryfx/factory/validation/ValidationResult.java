package io.github.factoryfx.factory.validation;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.util.LanguageText;

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

    public ValidationError createValidationError(Attribute<?,?> attribute, FactoryBase<?,?> parent, String attributeVariableName){
        return new ValidationError(languageText,attribute,parent,attributeVariableName);
    }


}
