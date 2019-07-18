package io.github.factoryfx.factory.builder;


import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.ChangeSummaryCreator;
import io.github.factoryfx.factory.storage.filesystem.FileSystemDataStorage;
import io.github.factoryfx.factory.storage.inmemory.InMemoryDataStorage;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.exception.FactoryExceptionHandler;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributePathTarget;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * Microservice without a persistence data storage
 *
 * default setup uses the {@link InMemoryDataStorage}
 *
 * @param <L> root liveobject
 * @param <R> Root
 * @param <S> Summary
 */
public class MicroserviceBuilder<L,R extends FactoryBase<L,R>,S> {

    private final Class<R> rootClass;
    private final R initialFactory;
    private DataStorageCreator<R,S> dataStorageCreator;
    private ChangeSummaryCreator<R,S> changeSummaryCreator;
    private FactoryExceptionHandler factoryExceptionHandler;
    private MigrationManager<R,S> migrationManager;
    private final SimpleObjectMapper objectMapper;

    public MicroserviceBuilder(Class<R> rootClass, R initialFactory, FactoryTreeBuilder<L,R,S> factoryTreeBuilder, SimpleObjectMapper objectMapper) {
        this.rootClass = rootClass;
        this.initialFactory = initialFactory;
        migrationManager = new MigrationManager<>(rootClass, objectMapper, new FactoryTreeBuilderAttributeFiller<>(factoryTreeBuilder));
        this.objectMapper = objectMapper;
    }

    public Microservice<L,R,S> build(){
        if (dataStorageCreator ==null) {
            dataStorageCreator =(initialData, migrationManager, objectMapper)->new InMemoryDataStorage<>(initialData);
        }
        if (factoryExceptionHandler == null) {
            factoryExceptionHandler = new RethrowingFactoryExceptionHandler();
        }

        return new Microservice<>(new FactoryManager<>(factoryExceptionHandler), dataStorageCreator.createDataStorage(initialFactory, migrationManager, objectMapper),changeSummaryCreator);
    }

    public MigrationManager<R,S> buildMigrationManager(){
        return migrationManager;
    }

    /**
     * changeSummaryCreator for history metadata
     * @param changeSummaryCreator changeSummaryCreator
     * @return builder
     */
    public MicroserviceBuilder<L,R,S> withChangeSummaryCreator(ChangeSummaryCreator<R,S> changeSummaryCreator){
        this.changeSummaryCreator=changeSummaryCreator;
        return this;
    }


    /**
     * with filesystem data storage
     * @param path path
     * @return builder
     */
    public MicroserviceBuilder<L,R,S> withFilesystemStorage(Path path){
        dataStorageCreator =(initialData, migrationManager, objectMapper)->new FileSystemDataStorage<>(path, initialData, migrationManager, objectMapper);
        return this;
    }


    /**
     * @param dataStorageCreator data storage
     * @return builder
     */
    public MicroserviceBuilder<L,R,S> withStorage(DataStorageCreator<R,S> dataStorageCreator){
        this.dataStorageCreator=dataStorageCreator;
        return this;
    }

    public MicroserviceBuilder<L,R,S> withExceptionHandler(FactoryExceptionHandler factoryExceptionHandler){
        this.factoryExceptionHandler = factoryExceptionHandler;
        return this;
    }

    public <LO, F extends FactoryBase<LO,R>> MicroserviceBuilder<L,R,S> withRenameAttributeMigration(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?,?>> attributeNameProvider){
        this.migrationManager.renameAttribute(dataClass,previousAttributeName,attributeNameProvider);
        return this;
    }

    public MicroserviceBuilder<L,R,S> withRenameClassMigration(String previousDataClassNameFullQualified, Class<? extends FactoryBase<?,?>> newDataClass){
        this.migrationManager.renameClass(previousDataClassNameFullQualified,newDataClass);
        return this;
    }

    /**
     * restore data from removed data/attributes into the current model
     * select data based on Singleton type
     * @param singletonPreviousDataClass singletonPreviousDataClass
     * @param previousAttributeName previousAttributeName
     * @param valueClass valueClass
     * @param setter setter
     * @param <AV> attribute value
     * @return builder
     */
    public <AV> MicroserviceBuilder<L,R,S>  withMigrationRestoreAttributeMigration(String singletonPreviousDataClass, String previousAttributeName, Class<AV> valueClass, BiConsumer<R,AV> setter){
        this.migrationManager.restoreAttribute(singletonPreviousDataClass,previousAttributeName,valueClass,setter);
        return this;
    }

    /**
     * restore data from removed data/attributes into the current model
     * select data based on path
     * @param path path
     * @param setter setter
     * @param <AV> attribute value
     * @return builder
     */
    public <AV> MicroserviceBuilder<L,R,S>  withRestoreAttributeMigration(AttributePathTarget<AV> path, BiConsumer<R,AV> setter){
        this.migrationManager.restoreAttribute(path,setter);
        return this;
    }

}
