package de.factoryfx.data.attribute.types;

import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ByteArrayAttribute extends ImmutableValueAttribute<byte[],ByteArrayAttribute> {
    public ByteArrayAttribute() {
        super(byte[].class);
    }

    @JsonIgnore
    @Override
    public String getDisplayText() {
        if (get()!=null){
            return Base64.getEncoder().encodeToString(get());
        }
        return "<empty>";
    }

    @Override
    public boolean internal_mergeMatch(byte[] value) {
        return Arrays.equals(get(), value);
    }


}

