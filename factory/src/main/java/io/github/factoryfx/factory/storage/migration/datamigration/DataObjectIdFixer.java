package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DataObjectIdFixer {
    protected Set<String> visited = new HashSet<>();
    private final Map<String,DataJsonNode> idToDataJson;

    public DataObjectIdFixer(Map<String,DataJsonNode> idToDataJson) {
        this.idToDataJson = idToDataJson;
    }


    public void fixFactoryId(JsonNode attributeValue, Consumer<JsonNode> valueSetter){
        if (attributeValue == null){
            return;
        }
        String id=getId(attributeValue);
        if (id!=null && idToDataJson.containsKey(id)) {
            if (visited.add(id)) {
                valueSetter.accept(idToDataJson.get(id).getJsonNode());
            } else {
                valueSetter.accept(new TextNode(id));
            }
        }
    }

    private String getId(JsonNode jsonNode){
        if (jsonNode.isTextual()){
            return jsonNode.textValue();
        } else {
            if (jsonNode instanceof ObjectNode){
                DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
                if (dataJsonNode.isData()){
                    return new DataJsonNode((ObjectNode) jsonNode).getId();
                }
            }
        }
        return null;
    }
}