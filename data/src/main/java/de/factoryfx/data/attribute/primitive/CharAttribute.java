package de.factoryfx.data.attribute.primitive;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class CharAttribute extends ImmutableValueAttribute<Character,CharAttribute> {

    @JsonCreator
    CharAttribute(Character value) {
        super(Character.class);
        set(value);
    }

    public CharAttribute() {
        super(Character.class);
    }

}