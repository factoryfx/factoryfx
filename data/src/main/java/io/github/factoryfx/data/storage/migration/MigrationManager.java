package io.github.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.DataObjectIdResolver;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.jackson.SimpleObjectMapper;
import io.github.factoryfx.data.storage.RawFactoryDataAndMetadata;
import io.github.factoryfx.data.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.data.storage.StoredDataMetadata;
import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.data.storage.migration.datamigration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <R> root
 * @param <S> summary
 */
public class MigrationManager<R extends Data,S> {
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
    List<SingletonDataRestore<R,?>> singletonBasedRestorations = new ArrayList<>();
    List<PathDataRestore<R,?>> pathBasedRestorations = new ArrayList<>();


    public <D extends Data> void renameAttribute(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider){
        renameAttributeMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass){
        renameClassMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    public <V> void restoreAttribute(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter){
        singletonBasedRestorations.add(new SingletonDataRestore<>(singletonPreviousDataClass,previousAttributeName,valueClass,setter,objectMapper));
    }

    public <V> void restoreAttribute(AttributePath<V> path, BiConsumer<R,V> setter){
        pathBasedRestorations.add(new PathDataRestore<>(path,setter,objectMapper));
    }

    public R migrate(JsonNode rootNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
        DataJsonNode rootDataJson = new DataJsonNode((ObjectNode) rootNode);
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

        dataStorageMetadataDictionary.markRemovedAttributes();
        R root;
        try {
            root = objectMapper.treeToValue(rootNode, rootClass);
        } catch (RuntimeException e) {
            if (Throwables.getRootCause(e) instanceof DataObjectIdResolver.UnresolvableJsonIDException){
                rootDataJson.fixIdsDeepFromRoot(dataStorageMetadataDictionary);
                root = objectMapper.treeToValue(rootNode, rootClass);
            } else {
                throw e;
            }
        }

        root.internal().addBackReferences();

        attributeFiller.fillNewAttributes(root,dataStorageMetadataDictionary);

        for (SingletonDataRestore<R,?> restoration : singletonBasedRestorations) {
            if (restoration.canMigrate(dataStorageMetadataDictionary)) {
                restoration.migrate(dataJsonNodes,root);
            }
        }

        for (PathDataRestore<R,?> restoration : pathBasedRestorations) {
            if (restoration.canMigrate(dataStorageMetadataDictionary)) {
                restoration.migrate(rootDataJson,root);
            }
        }
        return root;
    }

    public String write(R root) {
        return objectMapper.writeValueAsString(root);
    }

    public String writeStorageMetadata(StoredDataMetadata<S> metadata) {
        return objectMapper.writeValueAsString(metadata);
    }

    public R read(JsonNode data, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return read(objectMapper.writeTree(data),dataStorageMetadataDictionary);
    }

    public R read(String data, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        JsonNode migratedData = objectMapper.readTree(data);
        return migrate(migratedData,dataStorageMetadataDictionary);
    }

    @SuppressWarnings("unchecked")
    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return objectMapper.readValue(data,StoredDataMetadata.class);
    }

    public ScheduledUpdateMetadata readScheduledFactoryMetadata(String data) {
        return objectMapper.readValue(data,ScheduledUpdateMetadata.class);
    }

    public String writeScheduledUpdateMetadata(ScheduledUpdateMetadata metadata) {
        return  objectMapper.writeValueAsString(metadata);
    }

    public String writeRawFactoryDataAndMetadata(R root, StoredDataMetadata<S> metadata) {
        RawFactoryDataAndMetadata<S> rawFactoryDataAndMetadata = new RawFactoryDataAndMetadata<>();
        rawFactoryDataAndMetadata.metadata=metadata;
        rawFactoryDataAndMetadata.root=objectMapper.writeValueAsTree(root);
        return objectMapper.writeValueAsString(rawFactoryDataAndMetadata);
    }

    @SuppressWarnings("unchecked")
    public RawFactoryDataAndMetadata<S> readRawFactoryDataAndMetadata(String data) {
        return objectMapper.readValue(data, RawFactoryDataAndMetadata.class);
    }

}

