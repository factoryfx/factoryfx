package de.factoryfx.factory.builder;

import de.factoryfx.data.storage.ChangeSummaryCreator;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.data.storage.migration.DataMigration;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.FactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Microservice without a persistence data storage
 *
 * @param <V> Visitor
 * @param <L> root liveobject
 * @param <R> Root
 * @param <S> Summary
 */
public class MicroserviceBuilder<V,L,R extends FactoryBase<L,V,R>,S> {

    private final Class<R> rootClass;
    private final R initialFactory;
    private final GeneralStorageFormat generalStorageFormat;
    private DataStorageCreator<R,S> dataStorageCreator;
    private ChangeSummaryCreator<R,S> changeSummaryCreator;
    private FactoryExceptionHandler factoryExceptionHandler;
    private List<DataMigration> dataMigrations = new ArrayList<>();

    public MicroserviceBuilder(Class<R> rootClass, R initialFactory, GeneralStorageFormat generalStorageFormat) {
        this.rootClass = rootClass;
        this.initialFactory = initialFactory;
        this.generalStorageFormat = generalStorageFormat;
    }

    public Microservice<V,L,R,S> build(){
        DataAndStoredMetadata<R,S> initialFactory = new DataAndStoredMetadata<>(this.initialFactory,
                new StoredDataMetadata<>(LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        "System",
                        "initial factory",
                        UUID.randomUUID().toString(),
                        null,
                        generalStorageFormat,
                        this.initialFactory.internal().createDataStorageMetadataDictionaryFromRoot()
                )
        );

        if (dataStorageCreator ==null) {
            dataStorageCreator =(initialData, migrationManager)->new InMemoryDataStorage<>(initialData.root);
        }
        if (factoryExceptionHandler == null) {
            factoryExceptionHandler = new RethrowingFactoryExceptionHandler();
        }

        MigrationManager<R,S> migrationManager = new MigrationManager<>(rootClass, new ArrayList<>(),generalStorageFormat, dataMigrations);
        return new Microservice<>(new FactoryManager<>(factoryExceptionHandler), dataStorageCreator.createDataStorage(initialFactory, migrationManager),changeSummaryCreator,generalStorageFormat);
    }

    /**
     * width inMemory data storage
     * @return builder
     */
    public MicroserviceBuilder<V,L,R,S> withInMemoryStorage(){
        new InMemoryDataStorage<>(initialFactory);
        return this;
    }

    /**
     * changeSummaryCreator for history metadata
     * @param changeSummaryCreator
     * @return builder
     */
    public MicroserviceBuilder<V,L,R,S> widthChangeSummaryCreator(ChangeSummaryCreator<R,S> changeSummaryCreator){
        this.changeSummaryCreator=changeSummaryCreator;
        return this;
    }


    /**
     * width filesystem data storage
     * @param path path
     * @return builder
     */
    public MicroserviceBuilder<V,L,R,S> withFilesystemStorage(Path path){
        dataStorageCreator =(initialData, migrationManager)->new FileSystemDataStorage<>(path, initialData, migrationManager);
        return this;
    }


    /**
     * @param dataStorageCreator data storage
     * @return builder
     */
    public MicroserviceBuilder<V,L,R,S> withStorage(DataStorageCreator<R,S> dataStorageCreator){
        this.dataStorageCreator=dataStorageCreator;
        return this;
    }

    public MicroserviceBuilder<V,L,R,S> withExceptionHandler(FactoryExceptionHandler factoryExceptionHandler){
        this.factoryExceptionHandler = factoryExceptionHandler;
        return this;
    }

    public MicroserviceBuilder<V,L,R,S> withDataMigration(DataMigration dataMigration){
        dataMigrations.add(dataMigration);
        return this;
    }


}
