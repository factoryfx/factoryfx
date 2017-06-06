package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LongAttribute extends ImmutableValueAttribute<Long> {

    @JsonCreator
    LongAttribute(Long value) {
        super(null,Long.class);
        set(value);
    }

    @JsonCreator
    LongAttribute(String value) {
        super(null,Long.class);
        set(Long.parseLong(value));
    }

    public LongAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Long.class);
    }

    @Override
    protected Attribute<Long> createNewEmptyInstance() {
        return new LongAttribute(metadata);
    }
}