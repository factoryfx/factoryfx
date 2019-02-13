package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DataMigrationManager {

    List<DataMigration> dataMigrations =new ArrayList<>();

    public <D extends Data> void renameAttribute(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider){
        dataMigrations.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass){
        dataMigrations.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    public void migrate(JsonNode jsonNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
        List<DataJsonNode> dataJsonNodes = readDataList(jsonNode);
        for (DataMigration migration : dataMigrations) {
            if (migration.canMigrate(dataStorageMetadataDictionary)) {
                migration.migrate(dataJsonNodes);
            }
        }
    }

    List<DataJsonNode> readDataList(JsonNode jsonNode){
        ArrayList<DataJsonNode> result = new ArrayList<>();
        readDataList(jsonNode, result);
        return result;

    }

    private void readDataList(JsonNode jsonNode, List<DataJsonNode> result){
        if (isData(jsonNode)) {
            result.add(new DataJsonNode((ObjectNode)jsonNode));
        }
        for (JsonNode element : jsonNode) {
            if (element.isArray()) {
                for (JsonNode arrayElement : element) {
                    readDataList(arrayElement, result);
                }
            } else {
                readDataList(element, result);
            }
        }

    }

    private boolean isData(JsonNode jsonNode){
        if (jsonNode.fieldNames().hasNext()){
            String fieldName = jsonNode.fieldNames().next();
            if ("@class".equals(fieldName)){
                return true;
            }
        }
        return false;
    }

    public interface DataMigration{
        boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary);
        void migrate(List<DataJsonNode> dataJsonNodes);
    }

    public static class ClassRename implements DataMigration{
        private final String previousDataClassNameFullQualified;
        private final Class<? extends Data> newDataClass;

        public ClassRename(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass) {
            this.previousDataClassNameFullQualified = previousDataClassNameFullQualified;
            this.newDataClass = newDataClass;
        }

        @Override
        public boolean canMigrate(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
            return dataStorageMetadataDictionary.containsClass(previousDataClassNameFullQualified);
        }

        public void migrate(List<DataJsonNode> dataJsonNodes) {
            dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(previousDataClassNameFullQualified)).forEach(dataJsonNode -> {
                dataJsonNode.renameClass(newDataClass);
            });
        }
    }

    public static class AttributeRename<D extends Data>  implements DataMigration{
        private final String dataClassNameFullQualified;
        private final String previousAttributeName;
        private String newAttributeName;

        public AttributeRename(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider) {
            this.dataClassNameFullQualified = dataClass.getName();
            this.previousAttributeName = previousAttributeName;

            D data = DataDictionary.getDataDictionary(dataClass).newInstance();
            Attribute<?, ?> newAttribute = attributeNameProvider.apply(data);
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute==newAttribute){
                    newAttributeName = attributeVariableName;
                }
            });
            if (this.newAttributeName==null){
                throw new IllegalArgumentException("wrong attributeNameProvider");
            }

        }

        public boolean canMigrate(DataStorageMetadataDictionary dataStorageMetadataDictionary){
            return dataStorageMetadataDictionary.containsClass(dataClassNameFullQualified) &&
                    dataStorageMetadataDictionary.containsAttribute(dataClassNameFullQualified,previousAttributeName);
        }

        public void migrate(List<DataJsonNode> dataJsonNodes) {
            dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(dataClassNameFullQualified)).forEach(dataJsonNode -> {
                dataJsonNode.renameAttribute(previousAttributeName,newAttributeName);
            });
        }
    }
}
