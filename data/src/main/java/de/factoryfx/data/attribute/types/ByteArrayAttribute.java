package de.factoryfx.data.attribute.types;

import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class ByteArrayAttribute extends ValueAttribute<byte[]> {
    public ByteArrayAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, byte[].class);
    }

    @JsonCreator
    ByteArrayAttribute(byte[] value) {
        super(null,null);
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
    public boolean internal_match(byte[] value) {
        return Arrays.equals(get(), value);
    }


}

