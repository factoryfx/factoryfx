package io.github.factoryfx.factory.storage.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import org.slf4j.LoggerFactory;

import io.github.factoryfx.factory.DataObjectIdResolver;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.RawFactoryDataAndMetadata;
import io.github.factoryfx.factory.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeClassRename;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeFiller;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributePathTarget;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeRename;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeRetype;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeValueListParser;
import io.github.factoryfx.factory.storage.migration.datamigration.AttributeValueParser;
import io.github.factoryfx.factory.storage.migration.datamigration.ClassRename;
import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;
import io.github.factoryfx.factory.storage.migration.datamigration.DataMigration;
import io.github.factoryfx.factory.storage.migration.datamigration.PathDataRestore;
import io.github.factoryfx.factory.storage.migration.datamigration.SingletonDataRestore;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

/**
 * @param <R> root
 */
public class MigrationManager<R extends FactoryBase<?,R>> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MigrationManager.class);

    private final Class<R> rootClass;
    private final SimpleObjectMapper objectMapper;
    private final AttributeFiller<R> attributeFiller;

    public MigrationManager(Class<R> rootClass, SimpleObjectMapper objectMapper, AttributeFiller<R> attributeFiller) {
        this.rootClass = rootClass;
        this.objectMapper = objectMapper;
        this.attributeFiller = attributeFiller;
    }

    List<AttributeRename<R,?,?>> renameAttributeMigrations =new ArrayList<>();
    List<ClassRename> renameClassMigrations =new ArrayList<>();
    List<AttributeClassRename> renameAttributeClassMigrations = new ArrayList<>();
    List<AttributeRetype<R,?,?,?,?>> retypeAttributeMigrations = new ArrayList<>();

    List<SingletonDataRestore<R,?>> singletonBasedRestorations = new ArrayList<>();
    List<PathDataRestore<R,?>> pathBasedRestorations = new ArrayList<>();

    List<VersionedConfigurationPatch> patches = new ArrayList<>();
    private Integer declaredConfigurationSchemaVersion;


    public <L,F extends FactoryBase<L,R>> void renameAttribute(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?,?>> attributeNameProvider){
        renameAttributeMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends FactoryBase<?,?>> newDataClass){
        renameClassMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    /**
     * migration for a renamed or moved custom attribute class whose serialized form is unchanged: rewrites the attribute
     * class name recorded in the stored metadata dictionary so the attribute is not detected as retyped (which would clear the stored values)
     * @param previousAttributeClassNameFullQualified previous fully qualified attribute class name
     * @param newAttributeClassNameFullQualified new fully qualified attribute class name
     */
    public void renameAttributeClass(String previousAttributeClassNameFullQualified, String newAttributeClassNameFullQualified){
        renameAttributeClassMigrations.add(new AttributeClassRename(previousAttributeClassNameFullQualified,newAttributeClassNameFullQualified));
    }

    /**
     * migration for an attribute whose type changed, converting the previously stored single value to the new attribute value
     * @param dataClass factory class containing the attribute
     * @param previousValueClass previously stored value class
     * @param attributeNameProvider provider for the new attribute, e.g. {@code (f)->f.stringListAttribute}
     * @param converter converts the old value (may be null) to the new value, null result clears the attribute
     */
    public <L,F extends FactoryBase<L,R>,VOld,VNew> void retypeAttribute(Class<F> dataClass, Class<VOld> previousValueClass, Function<F, Attribute<VNew,?>> attributeNameProvider, Function<VOld,VNew> converter){
        retypeAttributeMigrations.add(new AttributeRetype<>(dataClass,previousValueClass,attributeNameProvider,converter,objectMapper));
    }

    /**
     * migration for a list attribute whose type changed, converting the previously stored list to the new attribute value
     * @param dataClass factory class containing the attribute
     * @param previousElementClass previously stored list element class
     * @param attributeNameProvider provider for the new attribute
     * @param converter converts the old list (may be null) to the new value, null result clears the attribute
     */
    public <L,F extends FactoryBase<L,R>,VOldElement,VNew> void retypeListAttribute(Class<F> dataClass, Class<VOldElement> previousElementClass, Function<F, Attribute<VNew,?>> attributeNameProvider, Function<List<VOldElement>,VNew> converter){
        retypeAttributeMigrations.add(AttributeRetype.listSource(dataClass,previousElementClass,attributeNameProvider,converter,objectMapper));
    }

    public <V> void restoreAttribute(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter){
        singletonBasedRestorations.add(new SingletonDataRestore<>(singletonPreviousDataClass,previousAttributeName,valueClass,setter,objectMapper));
    }

    public <V> void restoreAttribute(Class<V> clazz, AttributePathTarget<V> path, BiConsumer<R,V> setter){
        pathBasedRestorations.add(new PathDataRestore<>(path,setter,new AttributeValueParser<>(objectMapper,clazz)));
    }

    public <V> void restoreListAttribute(Class<V> clazz, AttributePathTarget<List<V>> path, BiConsumer<R,List<V>> setter){
        pathBasedRestorations.add(new PathDataRestore<>(path,setter,new AttributeValueListParser<>(new AttributeValueParser<>(objectMapper,clazz))));
    }

    /** register a patch applied on every load regardless of the configuration schema version */
    public void addPatch(ConfigurationPatch patch){
        patches.add(new VersionedConfigurationPatch(null,null,patch));
    }

    /** register a patch applied only when the stored configuration schema version equals fromVersion, afterwards the version is toVersion */
    public void addPatch(int fromVersion, int toVersion, ConfigurationPatch patch){
        patches.add(new VersionedConfigurationPatch(fromVersion,toVersion,patch));
    }

    /** declare the configuration schema version the application writes on save, defaults to the highest toVersion of the registered patches (0 if none) */
    public void setCurrentConfigurationSchemaVersion(int version){
        this.declaredConfigurationSchemaVersion=version;
    }

    public int getCurrentConfigurationSchemaVersion(){
        if (declaredConfigurationSchemaVersion!=null){
            return declaredConfigurationSchemaVersion;
        }
        int max=0;
        for (VersionedConfigurationPatch patch : patches) {
            if (!patch.isEveryTimePatch()){
                max=Math.max(max,patch.toVersion);
            }
        }
        return max;
    }

    /** validate the registered patch chain, called from the builder */
    public void validatePatches(){
        List<VersionedConfigurationPatch> versionedPatches = getSortedVersionedPatches();
        Map<Integer,VersionedConfigurationPatch> fromVersions = new java.util.HashMap<>();
        for (VersionedConfigurationPatch patch : patches) {
            if (patch.isEveryTimePatch()){
                continue;
            }
            if (patch.fromVersion==null || patch.toVersion==null){
                throw new IllegalStateException("patch must declare both fromVersion and toVersion or neither");
            }
            if (patch.toVersion<=patch.fromVersion){
                throw new IllegalStateException("patch toVersion ("+patch.toVersion+") must be greater than fromVersion ("+patch.fromVersion+")");
            }
            if (fromVersions.put(patch.fromVersion,patch)!=null){
                throw new IllegalStateException("duplicate patch fromVersion: "+patch.fromVersion);
            }
        }
        for (int i = 0; i < versionedPatches.size()-1; i++) {
            if (!versionedPatches.get(i).toVersion.equals(versionedPatches.get(i+1).fromVersion)){
                throw new IllegalStateException("gap in patch chain: patch ends at version "+versionedPatches.get(i).toVersion+" but next patch starts at "+versionedPatches.get(i+1).fromVersion);
            }
        }
        if (declaredConfigurationSchemaVersion!=null && !versionedPatches.isEmpty()){
            int maxToVersion = versionedPatches.get(versionedPatches.size()-1).toVersion;
            if (declaredConfigurationSchemaVersion<maxToVersion){
                throw new IllegalStateException("declared configuration schema version ("+declaredConfigurationSchemaVersion+") is lower than the highest patch toVersion ("+maxToVersion+")");
            }
        }
    }

    private List<VersionedConfigurationPatch> getSortedVersionedPatches(){
        return patches.stream()
                .filter(p->!p.isEveryTimePatch())
                .sorted(java.util.Comparator.comparing(p->p.fromVersion))
                .toList();
    }

    /**
     * apply the registered patches to the json of a loaded configuration (in-memory, nothing is written to the storage)
     * @param rootNode configuration json
     * @param metadata stored metadata of the configuration
     * @return the configuration schema version after patching
     */
    public int applyPatches(ObjectNode rootNode, StoredDataMetadata metadata){
        int version = metadata.configurationSchemaVersion==null ? 0 : metadata.configurationSchemaVersion;
        int currentVersion = getCurrentConfigurationSchemaVersion();
        DataJsonNode root = new DataJsonNode(rootNode);
        if (version>currentVersion){
            logger.warn("stored configuration schema version ({}) is newer than the application's ({}), version-gated patches are skipped. Was the application rolled back?",version,currentVersion);
        } else {
            for (VersionedConfigurationPatch patch : getSortedVersionedPatches()) {
                if (patch.fromVersion==version){
                    patch.patch.patch(root,metadata,objectMapper);
                    version=patch.toVersion;
                }
            }
        }
        for (VersionedConfigurationPatch patch : patches) {
            if (patch.isEveryTimePatch()){
                patch.patch.patch(root,metadata,objectMapper);
            }
        }
        return version;
    }

    /**
     * create a patch that persists the registered patches (including the bumped configuration schema version) via {@link io.github.factoryfx.factory.storage.DataStorage#patchAll}
     * @return patch
     */
    public ConfigurationPatch createConfigurationPatcher(){
        return (root, metadata, mapper) -> {
            //same order as on load: lift the stored json to the current model shape, then apply the patches.
            //the migrated json and the updated dictionary are persisted together with the patch results
            migrateJson(root.getJsonNode(), metadata.dataStorageMetadataDictionary);
            metadata.configurationSchemaVersion = applyPatches(root.getJsonNode(), metadata);
        };
    }

    public R read(JsonNode rootNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
        return read(rootNode, dataStorageMetadataDictionary, null);
    }

    /**
     * apply the declarative json migrations (rename/retype), the removal handling and the definition relocation,
     * lifting the stored json to the current model shape
     * @return the factory json nodes collected before the migrations ran (input for the restorations)
     */
    private List<DataJsonNode> migrateJson(JsonNode rootNode, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        DataJsonNode rootDataJson = new DataJsonNode((ObjectNode) rootNode);
        List<DataJsonNode> dataJsonNodes = rootDataJson.collectChildrenFromRoot();

        for (DataMigration migration : renameClassMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
                migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
            }
        }

        for (DataMigration migration : renameAttributeClassMigrations) {
            migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
        }

        for (DataMigration migration : renameAttributeMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
                migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
            }
        }

        for (DataMigration migration : retypeAttributeMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
                migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
            }
        }

        dataStorageMetadataDictionary.markRemovedAttributes();
        dataStorageMetadataDictionary.markRetypedAttributes();
        dataStorageMetadataDictionary.markRemovedClasses();

        //rescue factory definitions whose first occurrence sits inside a removed attribute/class before the removal drops them
        rootDataJson.relocateDefinitionsFromRemovedParts(dataStorageMetadataDictionary);

        List<DataJsonNode> migratedNodes = rootDataJson.collectChildrenFromRoot();
        //remove deleted attributes
        for (DataJsonNode dataJsonNode: migratedNodes) {
            dataJsonNode.applyRemovedAttribute(dataStorageMetadataDictionary);
        }
        //remove retyped attributes
        for (DataJsonNode dataJsonNode: migratedNodes) {
            dataJsonNode.applyRetypedAttribute(dataStorageMetadataDictionary);
        }
        //remove deleted classes
        for (DataJsonNode dataJsonNode: migratedNodes) {
            dataJsonNode.applyRemovedClasses(dataStorageMetadataDictionary);
        }
        return dataJsonNodes;
    }

    private R read(JsonNode rootNode, DataStorageMetadataDictionary dataStorageMetadataDictionary, Consumer<ObjectNode> configurationPatchApplier){
        DataJsonNode rootDataJson = new DataJsonNode((ObjectNode) rootNode);
        DataJsonNode previousRootDataJson = new DataJsonNode(rootNode.deepCopy());

        List<DataJsonNode> dataJsonNodes = migrateJson(rootNode, dataStorageMetadataDictionary);

        //configuration patches run after the declarative migrations and the removal handling: they see the stored
        //configuration lifted to the current model shape, so patches that roundtrip through the current factory
        //classes cannot silently drop data anymore
        if (configurationPatchApplier != null) {
            configurationPatchApplier.accept((ObjectNode) rootNode);
        }

        Map<String, DataJsonNode> idToChild = rootDataJson.collectChildrenMapFromRoot();

        R root;
        try {
            root = objectMapper.treeToValue(rootNode, rootClass);
        } catch (RuntimeException e) {
            if (Throwables.getRootCause(e) instanceof DataObjectIdResolver.UnresolvableJsonIDException){
                rootDataJson.fixIdsDeepFromRoot(idToChild);
                root = objectMapper.treeToValue(rootNode, rootClass);
            } else {
                throw e;
            }
        }

        for (SingletonDataRestore<R,?> restoration : singletonBasedRestorations) {
            if (restoration.canMigrate(dataStorageMetadataDictionary)) {
                restoration.migrate(dataJsonNodes,root);
            }
        }

        for (PathDataRestore<R,?> restoration : pathBasedRestorations) {
            if (restoration.canMigrate(dataStorageMetadataDictionary,previousRootDataJson)) {
                restoration.migrate(previousRootDataJson,root);
            }
        }

        root.internal().finalise();
        attributeFiller.fillNewAttributes(root,dataStorageMetadataDictionary);
        root.internal().fixDuplicateFactories();
        root.internal().finalise();
        return root;
    }

    public R read(String data, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return read(objectMapper.readTree(data), dataStorageMetadataDictionary);
    }

    /**
     * read a stored configuration: the declarative migrations, removal handling and definition relocation run first,
     * then the registered {@link ConfigurationPatch}es are applied (in-memory) on the current-model-shaped json
     * @param rootNode configuration json
     * @param metadata stored metadata of the configuration
     * @return root factory
     */
    public R read(JsonNode rootNode, StoredDataMetadata metadata) {
        return read(rootNode, metadata.dataStorageMetadataDictionary, (root) -> applyPatches(root, metadata));
    }

    public R read(String data, StoredDataMetadata metadata) {
        return read(objectMapper.readTree(data), metadata);
    }

    public StoredDataMetadata readStoredFactoryMetadata(JsonNode data, boolean light) {
        return light ?
                StoredDataMetadata.createLightStoredDataMetadata(data) :
                objectMapper.readValue(data,StoredDataMetadata.class);
    }

    public StoredDataMetadata readStoredFactoryMetadata(String data, boolean light) {
        return readStoredFactoryMetadata(objectMapper.readTree(data), light);
    }

    public ScheduledUpdateMetadata readScheduledFactoryMetadata(JsonNode data) {
        return objectMapper.readValue(data,ScheduledUpdateMetadata.class);
    }

    public ScheduledUpdateMetadata readScheduledFactoryMetadata(String data) {
        return objectMapper.readValue(data,ScheduledUpdateMetadata.class);
    }

    public String writeRawFactoryDataAndMetadata(R root, StoredDataMetadata metadata) {
        RawFactoryDataAndMetadata rawFactoryDataAndMetadata = new RawFactoryDataAndMetadata();
        rawFactoryDataAndMetadata.metadata=metadata;
        rawFactoryDataAndMetadata.root=objectMapper.valueToTree(root);
        return objectMapper.writeValueAsString(rawFactoryDataAndMetadata);
    }

    public String writeRawFactoryDataAndMetadata(RawFactoryDataAndMetadata rawFactoryDataAndMetadata) {
        return objectMapper.writeValueAsString(rawFactoryDataAndMetadata);
    }

    public RawFactoryDataAndMetadata readRawFactoryDataAndMetadata(String data) {
        return objectMapper.readValue(data, RawFactoryDataAndMetadata.class);
    }
}

