package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.Map;

public class AttributePathTarget<V> {
    private final List<AttributePathElement> path;
    private final Class<V> valueClass;
    private final String attribute;
    private final int index;

    public AttributePathTarget(Class<V> valueClass, List<AttributePathElement> path, String attribute, int index) {
        this.valueClass = valueClass;
        this.path = path;
        this.attribute = attribute;
        this.index = index;
    }

    public V resolveAttributeValue(DataJsonNode root, SimpleObjectMapper simpleObjectMapper) {
        DataJsonNode current = root;

        for (AttributePathElement pathElement : this.path) {
            current = pathElement.getNext(current);
        }
        JsonNode attributeValue = current.getAttributeValue(attribute);
        if (attributeValue==null){
            return null;
        }
        if (attributeValue.isArray()) {
            attributeValue = attributeValue.get(index);
        }
        if (attributeValue.isTextual()){
            Map<String, DataJsonNode> idToDataJsonNodeMap = root.collectChildrenMapFromRoot();
            String id=attributeValue.asText();
            if (idToDataJsonNodeMap.containsKey(attributeValue.textValue())){
                return idToDataJsonNodeMap.get(id).asData(valueClass, simpleObjectMapper);
            }
        }
        return simpleObjectMapper.treeToValue(attributeValue,valueClass);
    }

    public boolean isPathToRemovedAttribute(DataStorageMetadataDictionary dictionary, DataJsonNode root) {
        DataJsonNode current = root;
        for (AttributePathElement pathElement : this.path) {
            current = pathElement.getNext(current);
        }
        DataStorageMetadata dataStorageMetadata =dictionary.getDataStorageMetadata(current.getDataClassName());
        if (dataStorageMetadata==null) {
            return true;
        }
        if (dataStorageMetadata.getAttribute(attribute)==null){
            return false;
        }
        return dataStorageMetadata.getAttribute(attribute).isRemoved();
    }


    public boolean match(AttributePathTarget<V> path) {
        if (this.path.size()!= path.path.size()){
            return false;
        }
        if (!attribute.equals(path.attribute)){
            return false;
        }
        for (int i = 0; i < this.path.size(); i++) {
            if (!this.path.get(i).match(path.path.get(i))){
                return false;
            }
        }
        return true;
    }
}
