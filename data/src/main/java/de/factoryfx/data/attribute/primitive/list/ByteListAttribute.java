package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class ByteListAttribute extends ValueListAttribute<Byte,ByteListAttribute> {
    @JsonCreator
    public ByteListAttribute() {
        super(Byte.class);
    }

}