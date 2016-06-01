package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class GenericValueAttribute<T> extends ValueAttribute<T> {
    public GenericValueAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata);
    }

    public GenericValueAttribute(AttributeMetadata<T> attributeMetadata, T defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public GenericValueAttribute(T value) {
        this((AttributeMetadata<T>) null);
        set(value);
    }
}
