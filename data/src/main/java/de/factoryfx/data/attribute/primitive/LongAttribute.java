package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class LongAttribute extends ImmutableValueAttribute<Long,LongAttribute> {

    @JsonCreator
    LongAttribute(Long value) {
        super(Long.class);
        set(value);
    }

    @JsonCreator
    LongAttribute(String value) {
        super(Long.class);
        set(Long.parseLong(value));
    }

    public LongAttribute() {
        super(Long.class);
    }

}