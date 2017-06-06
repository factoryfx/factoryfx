package de.factoryfx.data.attribute.types;

import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ByteArrayAttribute extends ImmutableValueAttribute<byte[]> {
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
    protected Attribute<byte[]> createNewEmptyInstance() {
        return new ByteArrayAttribute(metadata);
    }

    @Override
    public boolean internal_match(byte[] value) {
        return Arrays.equals(get(), value);
    }


}

