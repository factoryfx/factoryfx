package de.factoryfx.factory.builder;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.ChangeSummaryCreator;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.GeneralMigration;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
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
import java.util.function.Consumer;


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
    private final GeneralStorageMetadata generalStorageMetadata;
    private DataStorageCreator<R,S> dataStorageCreator;
    private ChangeSummaryCreator<R,S> changeSummaryCreator;
    private FactoryExceptionHandler factoryExceptionHandler;
    private List<GeneralMigration> generalStorageFormatMigrations = new ArrayList<>();
    private DataMigrationManager dataMigrationManager = new DataMigrationManager();
    private SimpleObjectMapper objectMapper;

    public MicroserviceBuilder(Class<R> rootClass, R initialFactory, GeneralStorageMetadata generalStorageMetadata) {
        this.rootClass = rootClass;
        this.initialFactory = initialFactory;
        this.generalStorageMetadata = generalStorageMetadata;
    }

    public Microservice<V,L,R,S> build(){
        DataAndStoredMetadata<R,S> initialFactory = new DataAndStoredMetadata<>(this.initialFactory,
                new StoredDataMetadata<>(LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        "System",
                        "initial factory",
                        UUID.randomUUID().toString(),
                        null, generalStorageMetadata,
                        this.initialFactory.internal().createDataStorageMetadataDictionaryFromRoot()
                )
        );

        if (dataStorageCreator ==null) {
            dataStorageCreator =(initialData, migrationManager)->new InMemoryDataStorage<>(initialData.root);
        }
        if (factoryExceptionHandler == null) {
            factoryExceptionHandler = new RethrowingFactoryExceptionHandler();
        }

        if (objectMapper ==null){
            objectMapper =ObjectMapperBuilder.build();
        }

        MigrationManager<R,S> migrationManager = new MigrationManager<>(rootClass, generalStorageFormatMigrations, generalStorageMetadata, dataMigrationManager, objectMapper);
        return new Microservice<>(new FactoryManager<>(factoryExceptionHandler), dataStorageCreator.createDataStorage(initialFactory, migrationManager),changeSummaryCreator, generalStorageMetadata);
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

    public MicroserviceBuilder<V,L,R,S> withDataMigration(DataMigrationManager dataMigration){
        this.dataMigrationManager =dataMigration;
        return this;
    }

    public MicroserviceBuilder<V,L,R,S> withDataMigration(Consumer<DataMigrationManager> dataMigrationAdder){
        dataMigrationAdder.accept(dataMigrationManager);
        return this;
    }

    public MicroserviceBuilder<V,L,R,S> withGeneralStorageFormatMigrations(GeneralMigration generalMigration){
        generalStorageFormatMigrations.add(generalMigration);
        return this;
    }

    public MicroserviceBuilder<V,L,R,S> withJacksonObjectMapper(SimpleObjectMapper objectMapper){
        this.objectMapper =objectMapper;;
        return this;
    }


}
