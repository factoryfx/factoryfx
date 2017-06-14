package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class FloatAttribute extends ImmutableValueAttribute<Float,FloatAttribute> {

    @JsonCreator
    FloatAttribute(Float value) {
        super(Float.class);
        set(value);
    }

    @JsonCreator
    FloatAttribute(Long value) {
        super(Float.class);
        set(value.floatValue());
    }
    public FloatAttribute() {
        super(Float.class);
    }

    @Override
    protected FloatAttribute createNewEmptyInstance() {
        return new FloatAttribute();
    }
}