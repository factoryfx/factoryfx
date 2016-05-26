package de.factoryfx.model.validation;

public class ObjectRequired<T> extends SimpleValidation<T> {
    public ObjectRequired() {
        super(o -> new ValidationResult(o != null, "required parameter"), "required parameter");
    }
}
