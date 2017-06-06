package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ByteAttribute extends ImmutableValueAttribute<Byte> {

    @JsonCreator
    ByteAttribute(Byte value) {
        super(null,Byte.class);
        set(value);
    }

    public ByteAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Byte.class);
    }

    @Override
    protected Attribute<Byte> createNewEmptyInstance() {
        return new ByteAttribute(metadata);
    }
}