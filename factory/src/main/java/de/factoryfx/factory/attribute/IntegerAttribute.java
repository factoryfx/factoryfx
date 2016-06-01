package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IntegerAttribute extends ValueAttribute<Integer> {

    public IntegerAttribute(AttributeMetadata<Integer> attributeMetadata) {
        super(attributeMetadata);
    }

    public IntegerAttribute(AttributeMetadata<Integer> attributeMetadata, Integer defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public IntegerAttribute(Integer value) {
        this((AttributeMetadata<Integer>) null);
        set(value);
    }

    @Override
    public Integer get() {
        return (Integer) super.get();
    }
}