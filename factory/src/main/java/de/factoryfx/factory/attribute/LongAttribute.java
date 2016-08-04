package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LongAttribute extends ValueAttribute<Long,LongAttribute> {

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
}