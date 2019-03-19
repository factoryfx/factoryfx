package io.github.factoryfx.data.attribute.types;

import io.github.factoryfx.data.attribute.ValueMapAttribute;

public class StringMapAttribute extends ValueMapAttribute<String,String,StringMapAttribute> {

    public StringMapAttribute() {
        super(String.class, String.class);
    }

}
