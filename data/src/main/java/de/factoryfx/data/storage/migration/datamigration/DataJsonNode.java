package de.factoryfx.data.storage.migration.datamigration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

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

}
