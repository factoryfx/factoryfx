package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;

public class IntegerListAttribute extends ValueListAttribute<Integer,IntegerListAttribute> {
    @JsonCreator
    public IntegerListAttribute() {
        super(Integer.class);
    }


    @Override
    protected IntegerListAttribute createNewEmptyInstance() {
        return new IntegerListAttribute();
    }
}