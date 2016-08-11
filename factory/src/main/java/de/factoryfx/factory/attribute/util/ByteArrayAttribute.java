package de.factoryfx.factory.attribute.util;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;
import de.factoryfx.factory.merge.attribute.ByteArrayMergeHelper;

public class ByteArrayAttribute extends ValueAttribute<byte[],ByteArrayAttribute> {
    public ByteArrayAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, byte[].class);
    }

    @JsonCreator
    ByteArrayAttribute(byte[] value) {
        super(null,null);
        set(value);
    }

    @Override
    public ByteArrayMergeHelper createMergeHelper() {
        return new ByteArrayMergeHelper(this);
    }

    @Override
    public String getDisplayText() {
        if (get()!=null){
            return Base64.getEncoder().encodeToString(get());
        }
        return "<empty>";
    }


}

