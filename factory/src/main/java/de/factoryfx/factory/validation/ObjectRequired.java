package de.factoryfx.factory.validation;

public class ObjectRequired<T> extends SimpleValidation<T> {
    public ObjectRequired() {
        super(o -> new ValidationResult(o != null, "required parameter"), "required parameter");
    }
}
