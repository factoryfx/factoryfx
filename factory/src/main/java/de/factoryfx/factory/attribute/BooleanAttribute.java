package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class BooleanAttribute extends ValueAttribute<Boolean> {

    public BooleanAttribute() {
        super();
        set(Boolean.FALSE);
    }

    @JsonCreator
    public BooleanAttribute(Boolean value) {
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
