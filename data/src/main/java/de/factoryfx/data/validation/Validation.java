package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

public interface Validation<T> {

    public LanguageText getValidationDescription();

    public boolean validate(T value);

}
