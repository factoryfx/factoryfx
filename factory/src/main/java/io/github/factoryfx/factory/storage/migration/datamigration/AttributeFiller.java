package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

/** fill new Attributes with structure from FactoryTreeBuilder s**/
@FunctionalInterface
public interface AttributeFiller<R extends FactoryBase>  {


    void fillNewAttributes(R root, DataStorageMetadataDictionary oldDataStorageMetadataDictionary);
}
