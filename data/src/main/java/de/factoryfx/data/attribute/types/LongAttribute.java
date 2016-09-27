package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class LongAttribute extends ValueAttribute<Long> {

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