package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AttributeValueListParser<V> implements BiFunction<JsonNode,Map<String, DataJsonNode>,List<V>> {

    private final AttributeValueParser<V> attributeValueParser;
    public AttributeValueListParser(AttributeValueParser<V> attributeValueParser) {
        this.attributeValueParser=attributeValueParser;
    }

    @Override
    public List<V> apply(JsonNode jsonNode, Map<String, DataJsonNode> idToDataJsonNodeMap) {
        ArrayList<V> result = new ArrayList<>();
        for (JsonNode child: jsonNode){
            result.add(attributeValueParser.apply(child,idToDataJsonNodeMap));
        }
        return result;
    }
}
