package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class BooleanAttribute extends ValueAttribute<Boolean> {

    public BooleanAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Boolean.class);
        set(Boolean.FALSE);
    }

    @JsonCreator
    BooleanAttribute(Boolean value) {
        super(null,Boolean.class);
        set(value);
    }

    @Override
    public Boolean get() {
        Boolean r = super.get();
        if (r == null) {
            return Boolean.FALSE;
        }
        return r;
    }

}
