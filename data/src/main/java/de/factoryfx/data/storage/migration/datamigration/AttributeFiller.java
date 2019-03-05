package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.BiConsumer;

/** fill new Attributes with structure from FactoryTreeBuilder s**/
@FunctionalInterface
public interface AttributeFiller<R extends Data>  {


    void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary);
}
