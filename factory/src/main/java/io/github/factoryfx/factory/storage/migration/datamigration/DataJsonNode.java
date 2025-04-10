package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        JsonNode attribute = jsonNode.get(attributeName);
        if (attribute==null){
            return null;
        }
        return new DataJsonNode((ObjectNode)attribute.get("v"));
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
            if (jsonNode == null) {
                if (this.jsonNode.get(attribute) instanceof ObjectNode) {
                    ((ObjectNode) this.jsonNode.get(attribute)).remove("v");
                }
            }
            if (this.jsonNode.get(attribute) instanceof ObjectNode) {
                ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
                this.jsonNode.set(attribute, objectNode);
                objectNode.set("v", jsonNode);
            } else if (this.jsonNode.get(attribute) instanceof ArrayNode) {
                this.jsonNode.set(attribute, jsonNode);
            }
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

    public <V> V getArrayAttributeValue(String attributeName, Class<V> valueClass, SimpleObjectMapper simpleObjectMapper, int index) {
        ArrayNode attributeValue = (ArrayNode) getAttributeValue(attributeName);
        return simpleObjectMapper.treeToValue(attributeValue.get(index), valueClass);
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

    public boolean isData(){
        return isData(this.jsonNode);
    }

    private void collectChildrenDeep(List<DataJsonNode> dataJsonNodes){
        for (JsonNode element : jsonNode) {
            if (element.isArray()) {
                for (JsonNode arrayElement : element) {
                    if (isData(arrayElement)) {
                        DataJsonNode child = new DataJsonNode((ObjectNode) arrayElement);
                        dataJsonNodes.add(child);
                        child.collectChildrenDeep(dataJsonNodes);
                    }
                }
            } else {
                if (isData(element.get("v"))) {
                    DataJsonNode child = new DataJsonNode((ObjectNode) element.get("v"));
                    dataJsonNodes.add(child);
                    child.collectChildrenDeep(dataJsonNodes);
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
        this.collectChildrenDeep(dataJsonNode);
        return dataJsonNode;
    }


    /**
     * reset the ids, (first occurrence is the object following are id references)
     * @param collected for recursion
     */
    private void replaceDuplicateFactoriesWidthIdDeep(HashSet<String> collected){
            this.visitAttributes((value, jsonNodeConsumer) -> {
                if (isData(value)) {
                    DataJsonNode child = new DataJsonNode((ObjectNode) value);
                    if (collected.add(child.getId())) {
                        child.replaceDuplicateFactoriesWidthIdDeep(collected);
                    } else {
                        jsonNodeConsumer.accept(new TextNode(child.getId()));
                    }
                }
            });
    }

    /**
     * replace all id refs with object
     * result is json without ids
     * @param idToDataJsonNode to resolve id
     */
    private void replaceIdRefsWidthFactoriesDeep(Map<String,DataJsonNode> idToDataJsonNode){
        this.visitAttributes((value, jsonNodeConsumer) -> {
            if (value.isTextual() && idToDataJsonNode.containsKey(value.asText())){
                jsonNodeConsumer.accept(idToDataJsonNode.get(value.asText()).jsonNode);
            }
            if (isData(value)){
                new DataJsonNode((ObjectNode) value).replaceIdRefsWidthFactoriesDeep(idToDataJsonNode);
            }
        });
    }


    //TODO return UUID
    public String getId() {
        return jsonNode.get("id").asText();
    }

    public <D> D asData(Class<D> valueClass, SimpleObjectMapper simpleObjectMapper) {
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

    Map<String, DataJsonNode> cache;
    public Map<String,DataJsonNode> collectChildrenMapFromRootCached() {
        if (cache==null){
            cache=collectChildrenMapFromRoot();
        }
        return cache;
    }

    public void applyRemovedAttribute(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        for (String attributeVariableName : this.getAttributes()) {
            if (dataStorageMetadataDictionary.isRemovedAttribute(getDataClassName(),attributeVariableName)){
                this.jsonNode.remove(attributeVariableName);
            }
        }
    }

    public void applyRetypedAttribute(DataStorageMetadataDictionary dataStorageMetadataDictionary){
        for (String attributeVariableName : this.getAttributes()) {
            if (dataStorageMetadataDictionary.isRetypedAttribute(getDataClassName(),attributeVariableName)){
                this.setAttributeValue(attributeVariableName,null);
            }
        }
    }

    public void applyRemovedClasses(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this.visitAttributes((jsonNode, jsonNodeConsumer) -> {
            if (isData(jsonNode)) {
                DataStorageMetadata dataStorageMetadata = dataStorageMetadataDictionary.getDataStorageMetadata(new DataJsonNode((ObjectNode) jsonNode).getDataClassName());
                if (dataStorageMetadata != null && dataStorageMetadata.isRemovedClass()) {
                    jsonNodeConsumer.accept(null);
                }
            }
        });
    }


    /**
     * fix objects in removed attributes.
     *
     * References are serialized using JsonIdentityInfo
     * That means first occurrence is the object and following are just the ids
     * If the first occurrence is a removed attribute Jackson can't read the reference.
     *
     * @param idToChild idToChild
     */
    public void fixIdsDeepFromRoot(Map<String, DataJsonNode> idToChild){
        replaceIdRefsWidthFactoriesDeep(idToChild);

        //reset the ids, (first occurrence is the object following are id references)
        this.replaceDuplicateFactoriesWidthIdDeep(new HashSet<>());
    }

    private void visitAttributes(BiConsumer<JsonNode,Consumer<JsonNode>> attributeConsumer) {
        for (String attributeVariableName : this.getAttributes()) {
            JsonNode attributeValue = this.getAttributeValue(attributeVariableName);
            if (attributeValue != null) {
                if (attributeValue.isArray()) {

                    List<Integer> removed = new ArrayList<>();
                    for (int i=0;i<(attributeValue).size();i++) {
                        final int setIndex=i;
                        attributeConsumer.accept(attributeValue.get(i), (value) -> {
                            if (value==null) {
                                removed.add(setIndex);
                            } else {
                                ((ArrayNode) attributeValue).set(setIndex, value);
                            }
                        });
                        for (Integer removedIndex : removed) {
                            ((ArrayNode) attributeValue).remove(removedIndex);
                        }
                    }
                } else {
                    attributeConsumer.accept(attributeValue, (value) -> this.setAttributeValue(attributeVariableName, value));
                }

            }
        }
    }


    public JsonNode getJsonNode(){
        return this.jsonNode;
    }

}
