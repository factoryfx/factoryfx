package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class DoubleAttribute extends ImmutableValueAttribute<Double,DoubleAttribute> {

    @JsonCreator
    DoubleAttribute(Double value) {
        super(Double.class);
        set(value);
    }

    @JsonCreator
    DoubleAttribute(Long value) {
        super(Double.class);
        set(value.doubleValue());
    }
    public DoubleAttribute() {
        super(Double.class);
    }

}