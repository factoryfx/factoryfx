package io.github.factoryfx.factory.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = { "isReference" })
public class AttributeStorageMetadata {

    @JsonProperty
    String variableName;
    @JsonProperty
    final String attributeClassName;
    @JsonProperty
    String referenceClass;

    @JsonCreator
    public AttributeStorageMetadata(@JsonProperty("variableName") String variableName, @JsonProperty("attributeClassName") String attributeClassName, @JsonProperty("referenceClass")String referenceClass) {
        this.variableName = variableName;
        this.attributeClassName = attributeClassName;
        this.referenceClass = referenceClass;
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean match(AttributeStorageMetadata attributeStorageMetadata) {
        return variableName.equals(attributeStorageMetadata.variableName) && attributeClassName.equals(attributeStorageMetadata.attributeClassName);
    }

    public void rename(String newAttributeName) {
        variableName=newAttributeName;
    }

    private boolean removed ;
    public void markRemoved() {
        removed=true;
    }

    @JsonIgnore
    public boolean isRemoved() {
        return removed;
    }

    private boolean retyped ;
    public void markRetyped() {
        retyped=true;
    }

    @JsonIgnore
    public boolean isRetyped() {
        return retyped;
    }

    public String getReferenceClass() {
        return referenceClass;
    }

    public void renameReferenceClass(String previousDataClassNameFullQualified, String newNameFullQualified) {
        if (previousDataClassNameFullQualified.equals(referenceClass) ){
            referenceClass=newNameFullQualified;
        }
    }
}