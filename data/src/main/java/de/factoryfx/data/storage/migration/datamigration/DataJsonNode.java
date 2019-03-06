package de.factoryfx.data.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void renameClass(Class<? extends Data> newDataClass) {
        jsonNode.set("@class",new TextNode(newDataClass.getName()));
    }

    public DataJsonNode getChild(String attributeName) {
        System.out.println(attributeName);
        return new DataJsonNode((ObjectNode)jsonNode.get(attributeName).get("v"));
    }

    public <V> V getAttributeValue(String attributeName, Class<V> valueClass) {
        return ObjectMapperBuilder.build().treeToValue(jsonNode.get(attributeName).get("v"), valueClass);
    }

    //IDs from JsonIdentityInfo
    public boolean isIdReference(String attributeName) {
        return jsonNode.get(attributeName).get("v").isTextual();
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

    /** get children including himself*/
    public List<DataJsonNode> collectChildrenFromRoot(){
        List<DataJsonNode> dataJsonNode = new ArrayList<>();
        dataJsonNode.add(this);
        this.collectChildren(dataJsonNode);
        return dataJsonNode;
    }

    public String getId() {
        return jsonNode.get("id").asText();
    }

    public <D/*extends Data*/> D asData(Class<D> valueClass) {
        return ObjectMapperBuilder.build().treeToValue(jsonNode, valueClass);
    }

}
