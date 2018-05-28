package de.factoryfx.data.validation;

import java.util.Collection;

import de.factoryfx.data.util.LanguageText;

public class ObjectRequired<T> implements Validation<T> {

    @Override
    public ValidationResult validate(T value) {
        boolean error = value == null;
        if (value instanceof Collection){
            error= ((Collection)value).isEmpty();
        }
        return new ValidationResult(error, getText());
    }

    protected LanguageText getText() {
        return new LanguageText().en("required parameter").de("Pflichtparameter");
    }
}
