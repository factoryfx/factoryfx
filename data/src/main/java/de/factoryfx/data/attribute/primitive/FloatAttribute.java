package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class FloatAttribute extends ImmutableValueAttribute<Float> {

    @JsonCreator
    FloatAttribute(Float value) {
        super(null,Float.class);
        set(value);
    }

    @JsonCreator
    FloatAttribute(Long value) {
        super(null,Float.class);
        set(value.floatValue());
    }
    public FloatAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Float.class);
    }

    @Override
    protected Attribute<Float> createNewEmptyInstance() {
        return new FloatAttribute(metadata);
    }
}