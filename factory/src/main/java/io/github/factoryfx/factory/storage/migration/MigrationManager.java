package io.github.factoryfx.factory.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;

import io.github.factoryfx.factory.DataObjectIdResolver;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.RawFactoryDataAndMetadata;
import io.github.factoryfx.factory.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.storage.migration.datamigration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <R> root
 */
public class MigrationManager<R extends FactoryBase<?,R>> {
    private final Class<R> rootClass;
    private final SimpleObjectMapper objectMapper;
    private final AttributeFiller<R> attributeFiller;

    public MigrationManager(Class<R> rootClass, SimpleObjectMapper objectMapper, AttributeFiller<R> attributeFiller) {
        this.rootClass = rootClass;
        this.objectMapper = objectMapper;
        this.attributeFiller = attributeFiller;
    }

    List<AttributeRename> renameAttributeMigrations =new ArrayList<>();
    List<ClassRename> renameClassMigrations =new ArrayList<>();
    List<AttributeRetype> retypeAttributeMigrations = new ArrayList<>();

    List<SingletonDataRestore<R,?>> singletonBasedRestorations = new ArrayList<>();
    List<PathDataRestore<R,?>> pathBasedRestorations = new ArrayList<>();





    public <L,F extends FactoryBase<L,R>> void renameAttribute(Class<F> dataClass, String previousAttributeName, Function<F, Attribute<?,?>> attributeNameProvider){
        renameAttributeMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends FactoryBase<?,?>> newDataClass){
        renameClassMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
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

    public R migrate(JsonNode rootNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
        DataJsonNode rootDataJson = new DataJsonNode((ObjectNode) rootNode);
        DataJsonNode previousRootDataJson = new DataJsonNode(rootNode.deepCopy());
        List<DataJsonNode> dataJsonNodes = rootDataJson.collectChildrenFromRoot();

        for (DataMigration migration : renameClassMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
                migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
            }
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

        Map<String, DataJsonNode> idToChild = rootDataJson.collectChildrenMapFromRoot();
        //remove deleted attributes
        for (DataJsonNode dataJsonNode: idToChild.values()) {
            dataJsonNode.applyRemovedAttribute(dataStorageMetadataDictionary);
        }
        //remove retyped attributes
        for (DataJsonNode dataJsonNode: idToChild.values()) {
            dataJsonNode.applyRetypedAttribute(dataStorageMetadataDictionary);
        }
        //remove deleted classes
        for (DataJsonNode dataJsonNode: idToChild.values()) {
            dataJsonNode.applyRemovedClasses(dataStorageMetadataDictionary);
        }

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

        root.internal().finalise();

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

        attributeFiller.fillNewAttributes(root,dataStorageMetadataDictionary);
        root.internal().fixDuplicateFactories();
        root.internal().finalise();
        return root;
    }

    public String write(R root) {
        return objectMapper.writeValueAsString(root);
    }

    public String writeStorageMetadata(StoredDataMetadata metadata) {
        return objectMapper.writeValueAsString(metadata);
    }

    public R read(JsonNode data, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return read(objectMapper.writeTree(data),dataStorageMetadataDictionary);
    }

    public R read(String data, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        JsonNode migratedData = objectMapper.readTree(data);
        return migrate(migratedData,dataStorageMetadataDictionary);
    }

    public StoredDataMetadata readStoredFactoryMetadata(String data) {
        return objectMapper.readValue(data,StoredDataMetadata.class);
    }

    public ScheduledUpdateMetadata readScheduledFactoryMetadata(String data) {
        return objectMapper.readValue(data,ScheduledUpdateMetadata.class);
    }

    public String writeScheduledUpdateMetadata(ScheduledUpdateMetadata metadata) {
        return  objectMapper.writeValueAsString(metadata);
    }

    public String writeRawFactoryDataAndMetadata(R root, StoredDataMetadata metadata) {
        RawFactoryDataAndMetadata rawFactoryDataAndMetadata = new RawFactoryDataAndMetadata();
        rawFactoryDataAndMetadata.metadata=metadata;
        rawFactoryDataAndMetadata.root=objectMapper.writeValueAsTree(root);
        return objectMapper.writeValueAsString(rawFactoryDataAndMetadata);
    }

    public RawFactoryDataAndMetadata readRawFactoryDataAndMetadata(String data) {
        return objectMapper.readValue(data, RawFactoryDataAndMetadata.class);
    }

}

