package io.github.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.DataDictionary;


import java.util.*;

public class DataStorageMetadata {
    @JsonProperty
    private String className;
    @JsonProperty
    private final List<AttributeStorageMetadata> attributes;
    @JsonProperty
    private final long count;

    @JsonCreator
    public DataStorageMetadata(@JsonProperty("attributes") List<AttributeStorageMetadata> attributes, @JsonProperty("className")String className, @JsonProperty("count")long count) {
        this.attributes=attributes;
        this.className = className;
        this.count = count;
    }

    private Map<String,AttributeStorageMetadata> nameToAttributeMap;
    public AttributeStorageMetadata getAttribute(String attributeName) {
        if (nameToAttributeMap ==null) {
            nameToAttributeMap =new HashMap<>();
            for (AttributeStorageMetadata attribute : attributes) {
                nameToAttributeMap.put(attribute.getVariableName(),attribute);
            }
        }
        return nameToAttributeMap.get(attributeName);
    }

    public String getClassName() {
        return className;
    }

    public boolean match(DataStorageMetadata dataStorageMetadata) {
        if (!className.equals(dataStorageMetadata.className)) {
            return false;
        }
        if (attributes.size()!=dataStorageMetadata.attributes.size()){
            return false;
        }
        for (int i = 0; i < attributes.size(); i++) {
            if (!attributes.get(i).match(dataStorageMetadata.attributes.get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean containsAttribute(String attributeName) {
        AttributeStorageMetadata attribute = getAttribute(attributeName);
        if (attribute!=null) {
            return true;
        }
        return false;
    }

    public boolean isRemovedAttribute(String attributeName) {
        AttributeStorageMetadata attribute = getAttribute(attributeName);
        if (attribute!=null) {
            return attribute.isRemoved();
        }
        return false;
    }

    public boolean isSingleton(){
        return count==1;
    }

    public void renameAttribute(String previousAttributeName, String newAttributeName) {
        AttributeStorageMetadata attribute = getAttribute(previousAttributeName);
        if (attribute!=null) {
            attribute.rename(newAttributeName);
            nameToAttributeMap=null;//reset cache
        }
    }

    public void removeAttribute(String attributeName) {
        AttributeStorageMetadata attribute = getAttribute(attributeName);
        if (attribute!=null) {
            attributes.remove(attribute);
            nameToAttributeMap=null;//reset cache
        }
    }

    public void renameClass(String previousDataClassNameFullQualified, String newNameFullQualified) {
        if (previousDataClassNameFullQualified.equals(className)){
            className=newNameFullQualified;
        }
        for (AttributeStorageMetadata attribute : attributes) {
            attribute.renameReferenceClass(previousDataClassNameFullQualified,newNameFullQualified);
        }
    }

    @SuppressWarnings("unchecked")
    public void markRemovedAttributes(){
        try {
            Class aClass = Class.forName(className);
            Data data = DataDictionary.getDataDictionary(aClass).newInstance();

            Set<String> currentAttributeVariableNames= new HashSet<>();
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> currentAttributeVariableNames.add(attributeVariableName));

            for (AttributeStorageMetadata attribute : attributes) {
                if (!currentAttributeVariableNames.contains(attribute.getVariableName())){
                    attribute.markRemoved();
                }
            }
        } catch (ClassNotFoundException e) {
            for (AttributeStorageMetadata attribute : attributes) {
                attribute.markRemoved();
            }
        }

    }


    public boolean isReferenceAttribute(String attributeName) {
        AttributeStorageMetadata attribute = getAttribute(attributeName);
        if (attribute!=null) {
            return attribute.isReference();
        }
        return false;
    }

    public DataStorageMetadata getChild(String attribute, DataStorageMetadataDictionary dictionary) {
        return dictionary.getDataStorageMetadata(getAttribute(attribute).getReferenceClass());
    }
}
