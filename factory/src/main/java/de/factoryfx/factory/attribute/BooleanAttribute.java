package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class BooleanAttribute extends ValueAttribute<Boolean,BooleanAttribute> {

    public BooleanAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        set(Boolean.FALSE);
    }

    @JsonCreator
    BooleanAttribute(Boolean value) {
        super(null);
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
