package io.github.factoryfx.data.storage.migration.datamigration;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

/** fill new Attributes with structure from FactoryTreeBuilder s**/
@FunctionalInterface
public interface AttributeFiller<R extends Data>  {


    void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary);
}
