package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class IntegerAttribute extends ValueAttribute<Integer> {

    @JsonCreator
    IntegerAttribute(Integer value) {
        super(null,Integer.class);
        set(value);
    }

    public IntegerAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Integer.class);
    }
}