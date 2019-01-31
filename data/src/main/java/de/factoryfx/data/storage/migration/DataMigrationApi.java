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

public class DataMigrationApi {

    List<AttributeRename<?>> attributeRenames =new ArrayList<>();
    List<ClassRename> classRenames=new ArrayList<>();

    public <D extends Data> void renameAttribute(Class<D> dataClass, String previousAttributeName, Function<D, Attribute<?,?>> attributeNameProvider){
        attributeRenames.add(new AttributeRename<>(dataClass,previousAttributeName,attributeNameProvider));
    }

    public void renameClass(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass){
        classRenames.add(new ClassRename(previousDataClassNameFullQualified,newDataClass));
    }

    public void migrate(JsonNode jsonNode){
        List<DataJsonNode> dataJsonNodes = readDataList(jsonNode);
        for (AttributeRename<?> migration : attributeRenames) {
            migration.migrate(dataJsonNodes);
        }
        for (ClassRename classRename: classRenames) {
            classRename.migrate(dataJsonNodes);
        }
    }

    public boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary) {
        for (AttributeRename<?> migration : attributeRenames) {
            if (!migration.canMigrate(pastDataStorageMetadataDictionary)){
                return false;
            }
        }
        return true;
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

    public static class ClassRename{
        private final String previousDataClassNameFullQualified;
        private final Class<? extends Data> newDataClass;

        public ClassRename(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass) {
            this.previousDataClassNameFullQualified = previousDataClassNameFullQualified;
            this.newDataClass = newDataClass;
        }

        public void migrate(List<DataJsonNode> dataJsonNodes) {
            dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(previousDataClassNameFullQualified)).forEach(dataJsonNode -> {
                dataJsonNode.renameClass(newDataClass);
            });
        }
    }

    public static class AttributeRename<D extends Data>{
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

        public boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary){
            return pastDataStorageMetadataDictionary.containsClass(dataClassNameFullQualified) &&
                   pastDataStorageMetadataDictionary.containsAttribute(dataClassNameFullQualified,previousAttributeName);
        }

        public void migrate(List<DataJsonNode> dataJsonNodes) {
            dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(dataClassNameFullQualified)).forEach(dataJsonNode -> {
                dataJsonNode.renameAttribute(previousAttributeName,newAttributeName);
            });
        }
    }
}
