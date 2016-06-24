package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IntegerAttribute extends ValueAttribute<Integer,IntegerAttribute> {

    @JsonCreator
    IntegerAttribute(Integer value) {
        super(null);
        set(value);
    }

    public IntegerAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
    }
}