package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.storage.migration.datamigration.AttributeRename;
import de.factoryfx.data.storage.migration.datamigration.ClassRename;
import de.factoryfx.data.storage.migration.datamigration.DataJsonNode;
import de.factoryfx.data.storage.migration.datamigration.DataMigration;
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

    void migrate(JsonNode jsonNode, DataStorageMetadataDictionary dataStorageMetadataDictionary){
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
            return "@class".equals(fieldName);
        }
        return false;
    }

}
