package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.*;

public class DataJsonNode {
    private final ObjectNode jsonNode;


    public DataJsonNode(ObjectNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public void removeAttribute(String name){
        jsonNode.remove(name);
    }

    public String getDataClassName(){
        return jsonNode.get("@class").textValue();
    }

    public boolean match(String dataClassNameFullQualified){
        return getDataClassName().equals(dataClassNameFullQualified);
    }

    public void renameAttribute(String previousAttributeName, String newAttributeName) {
        jsonNode.set(newAttributeName, jsonNode.get(previousAttributeName));
        jsonNode.remove(previousAttributeName);
    }

    public void renameClass(Class<? extends FactoryBase<?,?>> newDataClass) {
        jsonNode.set("@class",new TextNode(newDataClass.getName()));
    }

    public DataJsonNode getChild(String attributeName) {
        return new DataJsonNode((ObjectNode)jsonNode.get(attributeName).get("v"));
    }

    public DataJsonNode getChild(String attributeName, int index) {
        if (!jsonNode.get(attributeName).isArray()){
            throw new IllegalArgumentException("is not a reflist attribute: "+attributeName);
        }
        return new DataJsonNode((ObjectNode)jsonNode.get(attributeName).get(index));
    }

    public JsonNode getAttributeValue(String attribute) {
        if (jsonNode.get(attribute)==null){
            return null;
        }
        if (jsonNode.get(attribute).isArray()){
            return jsonNode.get(attribute);
        }
        return jsonNode.get(attribute).get("v");
    }

    public void setAttributeValue(String attribute, JsonNode jsonNode) {
        try {
            if (jsonNode==null) {
                ((ObjectNode)jsonNode.get(attribute)).remove("v");
            }
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            this.jsonNode.set(attribute, objectNode);
            objectNode.set("v",jsonNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V getAttributeValue(String attributeName, Class<V> valueClass, SimpleObjectMapper simpleObjectMapper) {
        JsonNode attributeValue = getAttributeValue(attributeName);
        if (attributeValue==null){
            return null;
        }
        return simpleObjectMapper.treeToValue(attributeValue, valueClass);
    }

    public String getAttributeIdValue(String attributeName) {
        return jsonNode.get(attributeName).get("v").asText();
    }

    private boolean isData(JsonNode jsonNode){
        if (jsonNode==null){
            return false;
        }
        if (jsonNode.fieldNames().hasNext()){
            String fieldName = jsonNode.fieldNames().next();
            return "@class".equals(fieldName);
        }
        return false;
    }

    public void collectChildren(List<DataJsonNode> dataJsonNodes){
        for (JsonNode element : jsonNode) {
            if (element.isArray()) {
                for (JsonNode arrayElement : element) {
                    if (isData(arrayElement)) {
                        DataJsonNode child = new DataJsonNode((ObjectNode) arrayElement);
                        dataJsonNodes.add(child);
                        child.collectChildren(dataJsonNodes);
                    }
                }
            } else {
                if (isData(element.get("v"))) {
                    DataJsonNode child = new DataJsonNode((ObjectNode) element.get("v"));
                    dataJsonNodes.add(child);
                    child.collectChildren(dataJsonNodes);
                }

            }
        }
    }


    /**
     *  get children including himself
     * @return children
     */
    public List<DataJsonNode> collectChildrenFromRoot(){
        List<DataJsonNode> dataJsonNode = new ArrayList<>();
        dataJsonNode.add(this);
        this.collectChildren(dataJsonNode);
        return dataJsonNode;
    }

    //TODO return UUID
    public String getId() {
        return jsonNode.get("id").asText();
    }

    public <D/*extends Data*/> D asData(Class<D> valueClass, SimpleObjectMapper simpleObjectMapper) {
        return simpleObjectMapper.treeToValue(jsonNode, valueClass);
    }

    public List<String> getAttributes(){
        ArrayList<String> result = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> field = jsonNode.fields();
        while (field.hasNext()) {
            Map.Entry<String, JsonNode> element = field.next();
            if (element.getValue().isObject()){
                result.add(element.getKey());
            }
            if (element.getValue().isArray()){
                result.add(element.getKey());
            }
        }
        return result;
    }

    public Map<String,DataJsonNode> collectChildrenMapFromRoot() {
        HashMap<String, DataJsonNode> result = new HashMap<>();
        for (DataJsonNode dataJsonNode : collectChildrenFromRoot()) {
            result.put(dataJsonNode.getId(),dataJsonNode);
        }
        return result;

    }

    public void applyRemovedAttribute(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        for (String attributeVariableName : this.getAttributes()) {
            if (dataStorageMetadataDictionary.isRemovedAttribute(getDataClassName(),attributeVariableName)){
                jsonNode.remove(attributeVariableName);
            }
        }

    }

    /**
     * fix objects in removed attributes.
     *
     * References are serialized using JsonIdentityInfo
     * That means first occurrence is the object and following are just the ids
     * If the first occurrence is a removed attribute Jackson can't read the reference.
     *
     * @param dataStorageMetadataDictionary dataStorageMetadataDictionary
     */
    public void fixIdsDeepFromRoot(DataStorageMetadataDictionary dataStorageMetadataDictionary){

        //to keep the same iteration order as jackson, delete removed attributes first
        Map<String, DataJsonNode> allIdToDataJson = collectChildrenMapFromRoot();
        for (DataJsonNode dataJsonNode : allIdToDataJson.values()) {
            dataJsonNode.applyRemovedAttribute(dataStorageMetadataDictionary);
        }



        Map<String, DataJsonNode> idToDataJsonAfterRemoved = collectChildrenMapFromRoot();
        DataObjectIdFixer dataObjectIdFixer = new DataObjectIdFixer(allIdToDataJson);
        for (DataJsonNode dataJsonNode : idToDataJsonAfterRemoved.values()) {
            for (String attributeVariableName : dataJsonNode.getAttributes()) {
                if (dataStorageMetadataDictionary.isReferenceAttribute(getDataClassName(),attributeVariableName)) {
                    JsonNode attributeValue = dataJsonNode.getAttributeValue(attributeVariableName);

                    if (attributeValue != null) {
                        if (attributeValue.isArray()) {
                            int index = 0;
                            for (JsonNode arrayElement : attributeValue) {
                                final int setIndex=index;
                                dataObjectIdFixer.fixFactoryId(arrayElement, (value) -> ((ArrayNode)attributeValue).set(setIndex,value));
                                index++;
                            }
                        } else {
                            dataObjectIdFixer.fixFactoryId(attributeValue, (value) -> setAttributeValue(attributeVariableName, value));
                        }

                    }
                }
            }
        }
    }


    public JsonNode getJsonNode(){
        return this.jsonNode;
    }

}
