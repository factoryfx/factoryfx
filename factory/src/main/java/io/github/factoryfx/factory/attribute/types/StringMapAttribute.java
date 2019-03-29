package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.attribute.ValueMapAttribute;

public class StringMapAttribute extends ValueMapAttribute<String,String,StringMapAttribute> {

    public StringMapAttribute() {
        super(String.class, String.class);
    }

}
