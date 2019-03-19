package io.github.factoryfx.data.validation;

@FunctionalInterface
public interface Validation<T> {
    ValidationResult validate(T value);
}
