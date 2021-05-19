package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.attribute.ValueMapAttribute;

public class BooleanMapAttribute extends ValueMapAttribute<String,Boolean, BooleanMapAttribute> {

    public BooleanMapAttribute() {
        super(String.class, Boolean.class);
    }

}
