package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

import java.util.Map;
import java.util.function.BiFunction;

public class AttributeValueParser<V> implements BiFunction<JsonNode,Map<String, DataJsonNode>,V> {

    private final SimpleObjectMapper simpleObjectMapper;
    private final Class<V> valueClass;

    public AttributeValueParser(SimpleObjectMapper simpleObjectMapper, Class<V> valueClass) {
        this.simpleObjectMapper = simpleObjectMapper;
        this.valueClass = valueClass;
    }

    @Override
    public V apply(JsonNode jsonNode, Map<String, DataJsonNode> idToDataJsonNodeMap) {
        if (FactoryBase.class.isAssignableFrom(valueClass) &&  jsonNode.isTextual()){
            String id=jsonNode.asText();
            if (idToDataJsonNodeMap.containsKey(id)){
                return simpleObjectMapper.treeToValue(idToDataJsonNodeMap.get(id).getJsonNode(),valueClass);
            }
        }
        return simpleObjectMapper.treeToValue(jsonNode,valueClass);
    }
}
