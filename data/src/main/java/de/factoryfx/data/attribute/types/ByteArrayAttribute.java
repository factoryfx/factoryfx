package de.factoryfx.data.attribute.types;

import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ByteArrayAttribute extends ImmutableValueAttribute<byte[],ByteArrayAttribute> {
    public ByteArrayAttribute() {
        super(byte[].class);
    }

    @JsonCreator
    ByteArrayAttribute(byte[] value) {
        super(null);
        set(value);
    }

    @Override
    public String getDisplayText() {
        if (get()!=null){
            return Base64.getEncoder().encodeToString(get());
        }
        return "<empty>";
    }

    @Override
    protected ByteArrayAttribute createNewEmptyInstance() {
        return new ByteArrayAttribute();
    }

    @Override
    public boolean internal_match(byte[] value) {
        return Arrays.equals(get(), value);
    }


}

