package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

public class ObjectRequired<T> implements Validation<T> {

    @Override
    public LanguageText getValidationDescription() {
        return new LanguageText().en("required parameter").de("Pflichtparameter");
    }

    @Override
    public boolean validate(T value) {
        return value != null;
    }
}
