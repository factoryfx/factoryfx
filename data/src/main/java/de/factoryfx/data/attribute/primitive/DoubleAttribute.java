package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class DoubleAttribute extends ImmutableValueAttribute<Double> {

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

    @Override
    protected Attribute<Double> createNewEmptyInstance() {
        return new DoubleAttribute(metadata);
    }
}