package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;

public class DoubleAttribute extends ValueAttribute<Double,DoubleAttribute> {

    @JsonCreator
    DoubleAttribute(Double value) {
        super(null,Double.class);
        set(value);
    }

    @JsonCreator
    DoubleAttribute(Long value) {
        super(null,Double.class);
        set(value.doubleValue());
    }
    public DoubleAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Double.class);
    }
}