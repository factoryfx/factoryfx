package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.Arrays;
import java.util.Base64;

class ByteArrayAttributeBase<A extends Attribute<byte[],A>>  extends ImmutableValueAttribute<byte[], A> {
    public ByteArrayAttributeBase() {
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

