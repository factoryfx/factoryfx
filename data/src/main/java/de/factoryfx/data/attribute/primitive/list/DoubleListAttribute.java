package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;

public class DoubleListAttribute extends ValueListAttribute<Double,DoubleListAttribute> {
    @JsonCreator
    public DoubleListAttribute() {
        super(Double.class);
    }


    @Override
    protected DoubleListAttribute createNewEmptyInstance() {
        return new DoubleListAttribute();
    }
}