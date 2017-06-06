package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class CharAttribute extends ImmutableValueAttribute<Character> {

    @JsonCreator
    CharAttribute(Character value) {
        super(null,Character.class);
        set(value);
    }

    public CharAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Character.class);
    }

    @Override
    protected Attribute<Character> createNewEmptyInstance() {
        return new CharAttribute(metadata);
    }
}