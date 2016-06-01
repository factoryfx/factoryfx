package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StringAttribute extends ValueAttribute<String> {
    private String defaultValue;

    public StringAttribute(AttributeMetadata<String> attributeMetadata) {
        super(attributeMetadata);
        defaultValue = null;
    }

    public StringAttribute(AttributeMetadata<String> attributeMetadata, String defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
        this.defaultValue = defaultValue;
    }

    @JsonCreator
    public StringAttribute(String value) {
        this((AttributeMetadata<String>) null);
        set(value);
    }

}
