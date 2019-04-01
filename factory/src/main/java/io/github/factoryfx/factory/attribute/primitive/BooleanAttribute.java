package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

public class BooleanAttribute extends ImmutableValueAttribute<Boolean,BooleanAttribute> {

    public BooleanAttribute() {
        super();
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
