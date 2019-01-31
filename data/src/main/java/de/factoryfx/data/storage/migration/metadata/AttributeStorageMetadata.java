package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributeStorageMetadata {

    @JsonProperty
    private final String variableName;
    @JsonProperty
    private final String attributeClassName;

    @JsonCreator
    public AttributeStorageMetadata(@JsonProperty("variableName") String variableName, @JsonProperty("attributeClassName") String attributeClassName) {
        this.variableName = variableName;
        this.attributeClassName = attributeClassName;
    }

    public String getAttributeClassName() {
        return attributeClassName;
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean match(AttributeStorageMetadata attributeStorageMetadata) {
        return variableName.equals(attributeStorageMetadata.variableName) && attributeClassName.equals(attributeStorageMetadata.attributeClassName);
    }
}