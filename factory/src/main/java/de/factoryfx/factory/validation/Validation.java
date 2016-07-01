package de.factoryfx.factory.validation;

import de.factoryfx.factory.util.LanguageText;

public interface Validation<T> {

    public LanguageText getValidationDescription();

    public boolean validate(T value);

}
