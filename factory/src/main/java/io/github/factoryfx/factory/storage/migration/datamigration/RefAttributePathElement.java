package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.Objects;

public class RefAttributePathElement implements AttributePathElement{
    final String pathElement;

    public RefAttributePathElement(String pathElement) {
        this.pathElement = pathElement;
    }

    @Override
    public DataJsonNode getNext(DataJsonNode current){
        return current.getChild(pathElement);
    }

    @Override
    public DataStorageMetadata getNext(DataStorageMetadata current, DataStorageMetadataDictionary dictionary) {
        return current.getChild(pathElement,dictionary);
    }

    @Override
    public boolean match(AttributePathElement attributePathElement) {
        if (this == attributePathElement) return true;
        if (attributePathElement == null || getClass() != attributePathElement.getClass()) return false;
        RefAttributePathElement that = (RefAttributePathElement) attributePathElement;
        return Objects.equals(pathElement, that.pathElement);
    }
}
