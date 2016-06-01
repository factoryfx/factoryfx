package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EnumAttribute<T extends Enum<T>> extends ValueAttribute<T> {

    public EnumAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata);
    }

    public EnumAttribute(AttributeMetadata<T> attributeMetadata, T value) {
        this(attributeMetadata);
        set(value);
    }

    @JsonCreator
    public EnumAttribute(T value) {
        this((AttributeMetadata<T>) null);
        set(value);
    }
}
