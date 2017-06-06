package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class ShortAttribute extends ImmutableValueAttribute<Short> {

    @JsonCreator
    ShortAttribute(Short value) {
        super(null,Short.class);
        set(value);
    }

    public ShortAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Short.class);
    }

    @Override
    protected Attribute<Short> createNewEmptyInstance() {
        return new ShortAttribute(metadata);
    }
}