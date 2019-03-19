package io.github.factoryfx.data.attribute.primitive;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;

public class BooleanAttribute extends ImmutableValueAttribute<Boolean,BooleanAttribute> {

    public BooleanAttribute() {
        super(Boolean.class);
        set(Boolean.FALSE);
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
