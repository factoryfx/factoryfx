package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;

public class ShortListAttribute extends ValueListAttribute<Short,ShortListAttribute> {
    @JsonCreator
    public ShortListAttribute() {
        super(Short.class);
    }


    @Override
    protected ShortListAttribute createNewEmptyInstance() {
        return new ShortListAttribute();
    }
}