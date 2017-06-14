package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class IntegerAttribute extends ImmutableValueAttribute<Integer,IntegerAttribute> {

    @JsonCreator
    IntegerAttribute(Integer value) {
        super(Integer.class);
        set(value);
    }

    public IntegerAttribute() {
        super(Integer.class);
    }

    @Override
    protected IntegerAttribute createNewEmptyInstance() {
        return new IntegerAttribute();
    }
}