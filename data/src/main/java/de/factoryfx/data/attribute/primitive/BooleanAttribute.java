package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class BooleanAttribute extends ImmutableValueAttribute<Boolean,BooleanAttribute> {

    public BooleanAttribute() {
        super(Boolean.class);
        set(Boolean.FALSE);
        nullable();
    }

    @JsonCreator
    BooleanAttribute(Boolean value) {
        super(Boolean.class);
        set(value);
        nullable();
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
