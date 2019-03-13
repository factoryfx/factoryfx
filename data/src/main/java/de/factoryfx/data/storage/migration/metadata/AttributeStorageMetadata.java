package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributeStorageMetadata {

    @JsonProperty
    String variableName;
    @JsonProperty
    final String attributeClassName;
    @JsonProperty
    final boolean isReference; // attribute that can contains ref ( and id json)
    @JsonProperty
    String referenceClass;

    @JsonCreator
    public AttributeStorageMetadata(@JsonProperty("variableName") String variableName, @JsonProperty("attributeClassName") String attributeClassName, @JsonProperty("isReference")boolean isReference, @JsonProperty("referenceClass")String referenceClass) {
        this.variableName = variableName;
        this.attributeClassName = attributeClassName;
        this.isReference = isReference;
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

    @JsonIgnore
    public boolean isReference() {
        return isReference;
    }

    private boolean removed ;
    public void markRemoved() {
        removed=true;
    }

    public boolean isRemoved() {
        return removed;
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