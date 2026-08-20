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
import io.github.factoryfx.factory.storage.migration.ConfigurationPatch;
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
        migrationManager.validatePatches();

        return new Microservice<>(new FactoryManager<>(factoryExceptionHandler), dataStorageCreator.createDataStorage(initialFactory, migrationManager, objectMapper), factoryTreeBuilder, migrationManager);
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
     * migration for a renamed or moved custom attribute class whose serialized form is unchanged (e.g. the attribute
     * class was moved to another package).<br>
     * The stored data tree does not contain the attribute class but the stored metadata dictionary does; a stale name
     * there marks the attribute as retyped on load, which clears the stored values. This migration rewrites the
     * recorded attribute class name (in every factory class that uses it) so the attribute keeps its values.
     * For an attribute whose serialized form changed use {@link #withRetypeAttributeMigration} instead.
     *
     * @param previousAttributeClassNameFullQualified
     *     previous fully qualified attribute class name
     * @param newAttributeClass
     *     new attribute class
     * @param <A>
     *     new attribute class (raw-typed bound so generic attribute classes can be passed as class literals)
     * @return builder
     */
    @SuppressWarnings("rawtypes")
    public <A extends Attribute> MicroserviceBuilder<L, R> withRenameAttributeClassMigration(String previousAttributeClassNameFullQualified, Class<A> newAttributeClass) {
        this.migrationManager.renameAttributeClass(previousAttributeClassNameFullQualified, newAttributeClass.getName());
        return this;
    }

    /**
     * migration for an attribute whose type changed, converting the previously stored single value to the new attribute value instead of dropping it
     *
     * @param dataClass
     *     factory class containing the attribute
     * @param previousValueClass
     *     previously stored value class
     * @param attributeNameProvider
     *     provider for the new attribute, e.g. {@code (f)->f.stringListAttribute}
     * @param converter
     *     converts the old value (may be null) to the new value, null result clears the attribute
     * @param <LO> liveobject
     * @param <F> factory containing the attribute
     * @param <VOld> previously stored value type
     * @param <VNew> value type of the new attribute
     * @return builder
     */
    public <LO, F extends FactoryBase<LO, R>, VOld, VNew> MicroserviceBuilder<L, R> withRetypeAttributeMigration(Class<F> dataClass, Class<VOld> previousValueClass, Function<F, Attribute<VNew, ?>> attributeNameProvider, Function<VOld, VNew> converter) {
        this.migrationManager.retypeAttribute(dataClass, previousValueClass, attributeNameProvider, converter);
        return this;
    }

    /**
     * migration for a list attribute whose type changed, converting the previously stored list to the new attribute value instead of dropping it
     *
     * @param dataClass
     *     factory class containing the attribute
     * @param previousElementClass
     *     previously stored list element class
     * @param attributeNameProvider
     *     provider for the new attribute
     * @param converter
     *     converts the old list (may be null) to the new value, null result clears the attribute
     * @param <LO> liveobject
     * @param <F> factory containing the attribute
     * @param <VOldElement> previously stored list element type
     * @param <VNew> value type of the new attribute
     * @return builder
     */
    public <LO, F extends FactoryBase<LO, R>, VOldElement, VNew> MicroserviceBuilder<L, R> withRetypeListAttributeMigration(Class<F> dataClass, Class<VOldElement> previousElementClass, Function<F, Attribute<VNew, ?>> attributeNameProvider, Function<List<VOldElement>, VNew> converter) {
        this.migrationManager.retypeListAttribute(dataClass, previousElementClass, attributeNameProvider, converter);
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

    /**
     * declare the configuration schema version the application stores with every saved configuration.<br>
     * optional, defaults to the highest toVersion of the registered patches (0 if none)
     *
     * @param version
     *     configuration schema version
     * @return builder
     */
    public MicroserviceBuilder<L, R> withConfigurationSchemaVersion(int version) {
        this.migrationManager.setCurrentConfigurationSchemaVersion(version);
        return this;
    }

    /**
     * register a patch applied only when the stored configuration schema version equals fromVersion (run-once semantics).<br>
     * the patch runs in-memory when a configuration (current or history) is loaded, afterwards the configuration's version is toVersion.
     * patches chain in version order. use {@link io.github.factoryfx.server.MicroserviceDeployment#persistConfigurationPatches()} to write patched
     * configurations including the bumped version back to the storage.
     * <p>
     * use for one-time data evolution (new defaults, newly wired factories, moved values): the patch runs once per
     * stored configuration, afterwards user edits to the patched data stick &ndash; idempotency alone can't provide
     * that, an idempotent every-time patch would re-impose its result on every load.
     * <p>
     * ordering guarantees on load: the declarative migrations (rename/retype), the removed/retyped attribute handling
     * and the definition relocation run first, then the version-gated patch chain, then the every-time patches
     * ({@link #withPatch(ConfigurationPatch)}). Patches therefore always see the stored configuration lifted to the
     * current model shape &ndash; patches that roundtrip through the current factory classes are safe by construction.
     *
     * @param fromVersion
     *     version the patch applies to
     * @param toVersion
     *     version after the patch, must be greater than fromVersion
     * @param patch
     *     patch
     * @return builder
     */
    public MicroserviceBuilder<L, R> withPatch(int fromVersion, int toVersion, ConfigurationPatch patch) {
        this.migrationManager.addPatch(fromVersion, toVersion, patch);
        return this;
    }

    /**
     * register a patch applied in-memory on every load of a configuration (current or history) regardless of the
     * configuration schema version. Every-time patches must be idempotent.
     * <p>
     * use to enforce environment/technical invariants that must hold regardless of later user edits (e.g. overwriting
     * database settings per environment). NOT for one-time data changes: an every-time patch re-imposes its result on
     * every load and would silently revert deliberate user changes &ndash; use
     * {@link #withPatch(int, int, ConfigurationPatch)} for those.
     * <p>
     * ordering guarantees on load: every-time patches run after the declarative migrations (rename/retype), the
     * removed/retyped attribute handling and the version-gated patch chain
     * ({@link #withPatch(int, int, ConfigurationPatch)}). Patches therefore always see the stored configuration lifted
     * to the current model shape &ndash; patches that roundtrip through the current factory classes are safe by
     * construction.
     *
     * @param patch
     *     patch
     * @return builder
     */
    public MicroserviceBuilder<L, R> withPatch(ConfigurationPatch patch) {
        this.migrationManager.addPatch(patch);
        return this;
    }

}
