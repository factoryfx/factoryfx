package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.attribute.ValueMapAttribute;

public class ObjectMapAttribute extends ValueMapAttribute<String,Object, ObjectMapAttribute> {

    public ObjectMapAttribute() {
        super(String.class, Object.class);
    }

}
