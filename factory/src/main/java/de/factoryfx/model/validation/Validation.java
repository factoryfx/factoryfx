package de.factoryfx.model.validation;

public interface Validation<T> {

    default String getValidationDescription() {
        return getClass().getSimpleName();
    }

    default ValidationResult validate(T value) {
        return new ValidationResult();
    }

}
