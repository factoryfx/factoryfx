package io.github.factoryfx.factory.validation;

@FunctionalInterface
public interface Validation<T> {
    ValidationResult validate(T value);
}
