package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.DataStorage;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.FactoryBase;

@FunctionalInterface
public interface DataStorageCreator<R extends FactoryBase<?,R>,S> {
    DataStorage<R,S> createDataStorage(R initialFactory, MigrationManager<R,S> migrationManager, SimpleObjectMapper objectMapper);
}
