package io.github.factoryfx.factory.builder;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.exception.FactoryExceptionHandler;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.filesystem.FileSystemDataStorage;
import io.github.factoryfx.factory.storage.inmemory.InMemoryDataStorage;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributePathTarget;
import io.github.factoryfx.factory.storage.migration.datamigration.PathBuilder;
import io.github.factoryfx.server.Microservice;

/**
 * Microservice without a persistence data storage
 * <p>
 * default setup uses the {@link InMemoryDataStorage}
 *
 * @param <L>
 *     root liveobject
 * @param <R>
 *     Root
 */
public class MicroserviceBuilder<L, R extends FactoryBase<L, R>> {

    private final R initialFactory;
    private DataStorageCreator<R> dataStorageCreator;
    private FactoryExceptionHandler<L, R> factoryExceptionHandler;
    private final MigrationManager<R> migrationManager;
    private final SimpleObjectMapper objectMapper;
    private final FactoryTreeBuilder<L, R> factoryTreeBuilder;

    public MicroserviceBuilder(Class<R> rootClass, R initialFactory, FactoryTreeBuilder<L, R> factoryTreeBuilder, SimpleObjectMapper objectMapper) {
        this.initialFactory = initialFactory;
        this.migrationManager = new MigrationManager<>(rootClass, objectMapper, new FactoryTreeBuilderAttributeFiller<>(factoryTreeBuilder));
        this.objectMapper = objectMapper;
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public Microservice<L, R> build() {
        if (dataStorageCreator == null) {
            dataStorageCreator = (initialData, migrationManager, objectMapper) -> new InMemoryDataStorage<>(initialData);
        }
        if (factoryExceptionHandler == null) {
            factoryExceptionHandler = new RethrowingFactoryExceptionHandler<>();
        }

        return new Microservice<>(new FactoryManager<>(factoryExceptionHandler), dataStorageCreator.createDataStorage(initialFactory, migrationManager, objectMapper), factoryTreeBuilder);
    }

    public MigrationManager<R> buildMigrationManager() {
        return migrationManager;
    }

    /**
     * with filesystem data storage
     *
     * @param path
     *     path
     * @return builder
     */
    public MicroserviceBuilder<L, R> withFilesystemStorage(Path path) {
        dataStorageCreator = (initialData, migrationManager, objectMapper) -> new FileSystemDataStorage<>(path, initialData, migrationManager, objectMapper);
        return this;
    }

    /**
     * with filesystem data storage
     *
     * @param path
     *     path
     * @param maxConfigurationHistory
     *     maximum number of historical configuration to keep
     * @return builder
     */
    public MicroserviceBuilder<L, R> withFilesystemStorage(Path path, int maxConfigurationHistory) {
        dataStorageCreator = (initialData, migrationManager, objectMapper) -> new FileSystemDataStorage<>(path, initialData, migrationManager, objectMapper, maxConfigurationHistory);
        return this;
    }

    /**
     * @param dataStorageCreator
     *     data storage
     * @return builder
     */
    public MicroserviceBuilder<L, R> withStorage(DataStorageCreator<R> dataStorageCreator) {
        this.dataStorageCreator = dataStorageCreator;
        return this;
    }

    public MicroserviceBuilder<L, R> withExceptionHandler(FactoryExceptionHandler<L, R> factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
        return this;
    }

    public <LO, F extends FactoryBase<LO, R>> MicroserviceBuilder<L, R> withRenameAttributeMigration(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?, ?>> attributeNameProvider) {
        this.migrationManager.renameAttribute(dataClass, previousAttributeName, attributeNameProvider);
        return this;
    }

    public MicroserviceBuilder<L, R> withRenameClassMigration(String previousDataClassNameFullQualified, Class<? extends FactoryBase<?, ?>> newDataClass) {
        this.migrationManager.renameClass(previousDataClassNameFullQualified, newDataClass);
        return this;
    }

    /**
     * restore data from removed data/attributes into the current model select data based on Singleton type
     *
     * @param singletonPreviousDataClass
     *     singletonPreviousDataClass
     * @param previousAttributeName
     *     previousAttributeName
     * @param valueClass
     *     valueClass
     * @param setter
     *     setter
     * @param <AV>
     *     attribute value
     * @return builder
     */
    public <AV> MicroserviceBuilder<L, R> withMigrationRestoreAttributeMigration(String singletonPreviousDataClass, String previousAttributeName, Class<AV> valueClass, BiConsumer<R, AV> setter) {
        this.migrationManager.restoreAttribute(singletonPreviousDataClass, previousAttributeName, valueClass, setter);
        return this;
    }

    /**
     * restore data from removed data/attributes into the current model select data based on path
     *
     * @param clazz
     *     value class
     * @param path
     *     path
     * @param setter
     *     setter
     * @param <AV>
     *     attribute value
     * @return builder
     */
    public <AV> MicroserviceBuilder<L, R> withRestoreAttributeMigration(Class<AV> clazz, AttributePathTarget<AV> path, BiConsumer<R, AV> setter) {
        this.migrationManager.restoreAttribute(clazz, path, setter);
        return this;
    }

    /**
     * @param clazz
     *     value class
     * @param pathCreator
     *     workaround for generics problems e.g.: {@code (path)->path.pathElement("x").attribute("attribute")}
     * @param setter
     *     setter
     * @param <AV>
     *     attribute value
     * @return builder
     * @see #withRestoreAttributeMigration(Class, AttributePathTarget, BiConsumer)
     */
    public <AV> MicroserviceBuilder<L, R> withRestoreAttributeMigration(Class<AV> clazz, Function<PathBuilder<AV>, AttributePathTarget<AV>> pathCreator, BiConsumer<R, AV> setter) {
        PathBuilder<AV> pathBuilder = new PathBuilder<>();
        this.migrationManager.restoreAttribute(clazz, pathCreator.apply(pathBuilder), setter);
        return this;
    }

    /**
     * restore data from removed list data/attributes into the current model select data based on path
     *
     * @param clazz
     *     value class
     * @param path
     *     path
     * @param setter
     *     setter
     * @param <AV>
     *     attribute value
     * @return builder
     */
    public <AV> MicroserviceBuilder<L, R> withRestoreListAttributeMigration(Class<AV> clazz, AttributePathTarget<List<AV>> path, BiConsumer<R, List<AV>> setter) {
        this.migrationManager.restoreListAttribute(clazz, path, setter);
        return this;
    }

}
