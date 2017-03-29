package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

public interface Validation<T> {

    LanguageText getValidationDescription();

    boolean validate(T value);

}
