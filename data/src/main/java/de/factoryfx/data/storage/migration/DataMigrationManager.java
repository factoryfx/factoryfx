package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataObjectIdResolver;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.datamigration.*;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataMigrationManager<R extends Data> {

    List<AttributeRename> renameAttributeMigrations =new ArrayList<>();
    List<ClassRename> renameClassMigrations =new ArrayList<>();
    private final Class<R> rootClass;
    List<SingletonDataRestore<R,?>> singletonBasedRestorations = new ArrayList<>();
    List<PathDataRestore<R,?>> pathBasedRestorations = new ArrayList<>();


    private final AttributeFiller<R> attributeFiller;
    public DataMigrationManager(AttributeFiller<R> attributeFiller, Class<R> rootClass){
        this.attributeFiller=attributeFiller;
        this.rootClass = rootClass;
    }

    public <D extends Data> void renameAttribute(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider){
        renameAttributeMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass){
        renameClassMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    public <V> void restoreAttribute(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter){
        singletonBasedRestorations.add(new SingletonDataRestore<>(singletonPreviousDataClass,previousAttributeName,valueClass,setter));
    }

    public <V> void restoreAttribute(AttributePath<V> path, BiConsumer<R,V> setter){
        pathBasedRestorations.add(new PathDataRestore<>(path,setter));
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
            root = ObjectMapperBuilder.build().treeToValue(rootNode, rootClass);
        } catch (RuntimeException e) {
            if (Throwables.getRootCause(e) instanceof DataObjectIdResolver.UnresolvableJsonIDException){
                rootDataJson.fixIdsDeepFromRoot(dataStorageMetadataDictionary);
                root = ObjectMapperBuilder.build().treeToValue(rootNode, rootClass);
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

}
