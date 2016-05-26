package de.factoryfx.model.validation;

import java.util.function.Function;

public class SimpleValidation<T> implements Validation<T> {

    private final Function<T, ValidationResult> validationFunction;
    private final String validationDescription;

    public SimpleValidation(Function<T, ValidationResult> validationFunction, String validationDescription) {
        this.validationFunction = validationFunction;
        this.validationDescription = validationDescription;
    }

    @Override
    public String getValidationDescription() {
        return validationDescription;
    }

    @Override
    public ValidationResult validate(T value) {
        return validationFunction.apply(value);
    }
}
