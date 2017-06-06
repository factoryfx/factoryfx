package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class IntegerAttribute extends ImmutableValueAttribute<Integer> {

    @JsonCreator
    IntegerAttribute(Integer value) {
        super(null,Integer.class);
        set(value);
    }

    public IntegerAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Integer.class);
    }

    @Override
    protected Attribute<Integer> createNewEmptyInstance() {
        return new IntegerAttribute(metadata);
    }
}