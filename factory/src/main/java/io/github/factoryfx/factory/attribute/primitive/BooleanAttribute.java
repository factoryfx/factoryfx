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
        return super.get();
    }

    @Override
    public String toString() {
        if (value!=null) {
            return String.valueOf(value);
        }
        return "BooleanAttribute value: null";
    }

}
