package de.factoryfx.data.validation;

import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class AttributeValidation<T> {
    private final Validation<T> validation;
    private final Attribute<?> attribute;

    public AttributeValidation(Validation<T> validation, Attribute<?> attribute) {
        this.validation = validation;
        this.attribute = attribute;
    }

    public Optional<ValidationError> validate(T data) {
        if (!validation.validate(data)){
            return Optional.of(new ValidationError(validation.getValidationDescription(),attribute));
        }
        return Optional.empty();
    }
}
