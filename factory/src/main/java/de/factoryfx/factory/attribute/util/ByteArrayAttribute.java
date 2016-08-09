package de.factoryfx.factory.attribute.util;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;

public class ByteArrayAttribute extends ValueAttribute<Byte[],ByteArrayAttribute> {
    public ByteArrayAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, Byte[].class);
    }

    @JsonCreator
    ByteArrayAttribute(Byte[] value) {
        super(null,null);
        set(value);
    }

    //byte vs Byte workaround
    public ByteArrayAttribute setBytes(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        set(bytes);
        return this;
    }
}

