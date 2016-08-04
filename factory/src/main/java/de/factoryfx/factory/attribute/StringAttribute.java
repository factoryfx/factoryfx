package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StringAttribute extends ValueAttribute<String,StringAttribute> {

    public StringAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,String.class);
    }

    @JsonCreator
    StringAttribute(String initialValue) {
        super(null,String.class);
        set(initialValue);
    }

}
