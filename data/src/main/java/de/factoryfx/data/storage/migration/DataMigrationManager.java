package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.datamigration.*;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataMigrationManager<R extends Data> {

    List<DataMigration> dataMigrations =new ArrayList<>();
    private final Class<R> rootClass;
    List<SingletonDataRestore<R,?>> restorations = new ArrayList<>();
    List<PathDataRestore<R,?>> restorations2 = new ArrayList<>();


    private final AttributeFiller<R> attributeFiller;
    public DataMigrationManager(AttributeFiller<R> attributeFiller, Class<R> rootClass){
        this.attributeFiller=attributeFiller;
        this.rootClass = rootClass;
    }

    public <D extends Data> void renameAttribute(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider){
        dataMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass){
        dataMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    public <V> void restoreAttribute(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter){
        restorations.add(new SingletonDataRestore<>(singletonPreviousDataClass,previousAttributeName,valueClass,setter));
    }

    public <V> void restoreAttribute(AttributePath<V> path, Class<V> valueClass, BiConsumer<R,V> setter){
        restorations2.add(new PathDataRestore<>(path,valueClass,setter));
    }

    R migrate(JsonNode jsonNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
        List<DataJsonNode> dataJsonNodes = new JsonDataUtility().readDataList(jsonNode);
        for (DataMigration migration : dataMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
                migration.updateDataStorageMetadataDictionary(dataStorageMetadataDictionary);
            }
        }

        R root = ObjectMapperBuilder.build().treeToValue(jsonNode,rootClass);
        root.internal().addBackReferences();

        attributeFiller.fillNewAttributes(root,dataStorageMetadataDictionary);

        for (SingletonDataRestore restoration : restorations) {
            DataStorageMetadataDictionary currentDataStorageMetadataDictionaryFromRoot = root.internal().createDataStorageMetadataDictionaryFromRoot();
            if (restoration.canMigrate(dataStorageMetadataDictionary,currentDataStorageMetadataDictionaryFromRoot)) {
                restoration.migrate(dataJsonNodes,root);
            }
        }

        for (PathDataRestore restoration : restorations2) {
            DataStorageMetadataDictionary currentDataStorageMetadataDictionaryFromRoot = root.internal().createDataStorageMetadataDictionaryFromRoot();
            if (restoration.canMigrate(dataStorageMetadataDictionary,currentDataStorageMetadataDictionaryFromRoot)) {
                restoration.migrate(new DataJsonNode((ObjectNode)jsonNode),root);
            }
        }
        return root;
    }

}
