package de.factoryfx.data.storage.migration.datamigration;


import de.factoryfx.data.storage.migration.metadata.DataStorageMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.Objects;

public class RefListAttributePathElement implements AttributePathElement {
    private final String path;
    private final int index;

    public RefListAttributePathElement(String path, int index) {
        this.path = path;
        this.index = index;
    }

    @Override
    public DataJsonNode getNext(DataJsonNode current){
        return current.getChild(path,index);
    }

    @Override
    public DataStorageMetadata getNext(DataStorageMetadata current, DataStorageMetadataDictionary dictionary) {
        return current.getChild(path,dictionary);
    }

    @Override
    public boolean match(AttributePathElement attributePathElement) {
        if (this == attributePathElement) return true;
        if (attributePathElement == null || getClass() != attributePathElement.getClass()) return false;
        RefListAttributePathElement that = (RefListAttributePathElement) attributePathElement;
        return index == that.index && Objects.equals(path, that.path);
    }

}
