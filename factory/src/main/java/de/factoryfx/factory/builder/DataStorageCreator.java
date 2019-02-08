package de.factoryfx.factory.builder;

import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.FactoryBase;

@FunctionalInterface
public interface DataStorageCreator<R extends FactoryBase<?,?,R>,S> {
    DataStorage<R,S> createDataStorage(DataAndStoredMetadata<R,S> initialFactory, MigrationManager<R,S> migrationManager);
}
