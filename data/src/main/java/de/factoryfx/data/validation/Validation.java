package de.factoryfx.data.validation;

import de.factoryfx.data.util.LanguageText;

import java.util.Optional;

@FunctionalInterface
public interface Validation<T> {
    ValidationResult validate(T value);
}
