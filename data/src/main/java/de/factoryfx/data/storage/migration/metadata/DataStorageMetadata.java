package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataStorageMetadata that = (DataStorageMetadata) o;
        return Objects.equals(className, that.className) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, attributes);
    }

    public String getClassName() {
        return className;
    }

    public List<String> getRemovedAttributes(DataStorageMetadata previousMetadata) {
        ArrayList<String> result = new ArrayList<>();
        Map<String, AttributeStorageMetadata> currentNameToMetadata = attributes.stream().collect(Collectors.toMap(AttributeStorageMetadata::getVariableName, Function.identity()));
        Map<String, AttributeStorageMetadata> previousNameToMetadata = previousMetadata.attributes.stream().collect(Collectors.toMap(AttributeStorageMetadata::getVariableName, Function.identity()));
        for (String previousName : previousNameToMetadata.keySet()) {
            if (!currentNameToMetadata.containsKey(previousName)){
                result.add(previousName);
            }
        }
        return result;
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

    public boolean containsAttribute(String previousAttributeName) {
        for (AttributeStorageMetadata attribute : attributes) {
            if (attribute.getVariableName().equals(previousAttributeName)){
                return true;
            }
        }
        return false;
    }

    public boolean isSingleton(){
        return count==1;
    }

    public void renameAttribute(String previousAttributeName, String newAttributeName) {
        for (AttributeStorageMetadata attribute : attributes) {
            if (attribute.getVariableName().equals(previousAttributeName)){
                attribute.rename(newAttributeName);
            }
        }
    }

    public void renameClass(String newNameFullQualified) {
        className=newNameFullQualified;
    }
}
