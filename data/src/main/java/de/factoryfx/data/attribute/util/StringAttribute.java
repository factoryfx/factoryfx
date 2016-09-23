package de.factoryfx.data.attribute.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class StringAttribute extends ValueAttribute<String> {

    public StringAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,String.class);
    }

    @JsonCreator
    StringAttribute(String initialValue) {
        super(null,String.class);
        set(initialValue);
    }

}
