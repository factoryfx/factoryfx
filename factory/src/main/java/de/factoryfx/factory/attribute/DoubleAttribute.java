package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DoubleAttribute extends ValueAttribute<Double,DoubleAttribute> {

    @JsonCreator
    DoubleAttribute(Double value) {
        super(null);
        set(value);
    }

    public DoubleAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
    }
}