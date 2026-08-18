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
    String attributeClassName;
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
    private String retypedToAttributeClassName;
    private boolean retypedToList;
    private String retypedToReferenceClassName;
    public void markRetyped(String retypedToAttributeClassName, boolean retypedToList, String retypedToReferenceClassName) {
        retyped=true;
        this.retypedToAttributeClassName=retypedToAttributeClassName;
        this.retypedToList=retypedToList;
        this.retypedToReferenceClassName=retypedToReferenceClassName;
    }

    @JsonIgnore
    public boolean isRetyped() {
        return retyped;
    }

    @JsonIgnore
    public String getRetypedToAttributeClassName() {
        return retypedToAttributeClassName;
    }

    @JsonIgnore
    public boolean isRetypedToList() {
        return retypedToList;
    }

    /** reference class of the retype target attribute, null if the target is not a reference attribute */
    @JsonIgnore
    public String getRetypedToReferenceClassName() {
        return retypedToReferenceClassName;
    }

    @JsonIgnore
    public String getAttributeClassName() {
        return attributeClassName;
    }

    /** set the attribute class after an explicit retype migration, so the attribute is no longer flagged as retyped */
    public void retype(String newAttributeClassName) {
        this.attributeClassName=newAttributeClassName;
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