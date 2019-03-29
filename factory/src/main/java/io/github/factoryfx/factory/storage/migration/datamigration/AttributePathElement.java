package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

public interface AttributePathElement {
    DataJsonNode getNext(DataJsonNode current);
    DataStorageMetadata getNext(DataStorageMetadata current, DataStorageMetadataDictionary dictionary);

    boolean match(AttributePathElement attributePathElement);
}
