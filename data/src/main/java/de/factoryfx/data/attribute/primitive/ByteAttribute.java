package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ByteAttribute extends ImmutableValueAttribute<Byte,ByteAttribute> {

    @JsonCreator
    ByteAttribute(Byte value) {
        super(Byte.class);
        set(value);
    }

    public ByteAttribute() {
        super(Byte.class);
    }

}