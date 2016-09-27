package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class DoubleAttribute extends ValueAttribute<Double> {

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