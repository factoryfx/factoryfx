package de.factoryfx.factory.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ValueAttribute;

public class IntegerAttribute extends ValueAttribute<Integer,IntegerAttribute> {

    @JsonCreator
    IntegerAttribute(Integer value) {
        super(null,Integer.class);
        set(value);
    }

    public IntegerAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,Integer.class);
    }
}