package de.factoryfx.factory.validation;

public interface Validation<T> {

    default String getValidationDescription() {
        return getClass().getSimpleName();
    }

    default ValidationResult validate(T value) {
        return new ValidationResult();
    }

}
