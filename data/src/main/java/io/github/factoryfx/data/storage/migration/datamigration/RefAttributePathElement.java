package io.github.factoryfx.data.storage.migration.datamigration;


import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.Objects;

public class RefAttributePathElement implements AttributePathElement{
    final String path;

    public RefAttributePathElement(String path) {
        this.path = path;
    }

    @Override
    public DataJsonNode getNext(DataJsonNode current){
        return current.getChild(path);
    }

    @Override
    public DataStorageMetadata getNext(DataStorageMetadata current, DataStorageMetadataDictionary dictionary) {
        return current.getChild(path,dictionary);
    }

    @Override
    public boolean match(AttributePathElement attributePathElement) {
        if (this == attributePathElement) return true;
        if (attributePathElement == null || getClass() != attributePathElement.getClass()) return false;
        RefAttributePathElement that = (RefAttributePathElement) attributePathElement;
        return Objects.equals(path, that.path);
    }
}
