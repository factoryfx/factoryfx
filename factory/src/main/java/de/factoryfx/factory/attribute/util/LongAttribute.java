package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;

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