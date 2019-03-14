package de.factoryfx.data.storage.migration.datamigration;


import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.ArrayList;
import java.util.List;

public interface AttributePathElement {
    DataJsonNode getNext(DataJsonNode current);
    DataStorageMetadata getNext(DataStorageMetadata current, DataStorageMetadataDictionary dictionary);

    boolean match(AttributePathElement attributePathElement);
}
