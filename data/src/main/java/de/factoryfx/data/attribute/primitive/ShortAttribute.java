package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ShortAttribute extends ImmutableValueAttribute<Short,ShortAttribute> {

    @JsonCreator
    ShortAttribute(Short value) {
        super(Short.class);
        set(value);
    }

    public ShortAttribute() {
        super(Short.class);
    }

}