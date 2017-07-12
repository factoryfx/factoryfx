package de.factoryfx.data.attribute.primitive.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ValueListAttribute;

public class CharListAttribute extends ValueListAttribute<Character,CharListAttribute> {
    @JsonCreator
    public CharListAttribute() {
        super(Character.class);
    }

}