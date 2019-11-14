package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class AttributePathTarget<V> {
    private final List<AttributePathElement> path;
    private final String attribute;
    private final int index;

    public AttributePathTarget(List<AttributePathElement> path, String attribute, int index) {
        this.path = path;
        this.attribute = attribute;
        this.index = index;
    }


    public V resolveAttributeValue(DataJsonNode root, BiFunction<JsonNode,Map<String, DataJsonNode>,V> valueParser) {
        DataJsonNode current = root;

        for (AttributePathElement pathElement : this.path) {
            current = pathElement.getNext(current);
        }
        JsonNode attributeValue = current.getAttributeValue(attribute);
        if (attributeValue==null){
            return null;
        }
        if (attributeValue.isArray() && index>=0) {
            attributeValue = attributeValue.get(index);
        }
        Map<String, DataJsonNode> idToDataJsonNodeMap = root.collectChildrenMapFromRootCached();
        return valueParser.apply(attributeValue,idToDataJsonNodeMap);
    }

    public boolean isPathToRemovedAttribute(DataStorageMetadataDictionary dictionary, DataJsonNode root) {
        DataJsonNode current = root;
        for (AttributePathElement pathElement : this.path) {
            if (current==null){//broken path
                return false;
            }
            current = pathElement.getNext(current);
        }
        if (current==null){//broken path
            return false;
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

    /**
     * attribute with different type
     * @param dictionary dictionary
     * @param root root
     * @return true if different type
     */
    public boolean isPathToRetypedAttribute(DataStorageMetadataDictionary dictionary, DataJsonNode root) {
        DataJsonNode current = root;
        for (AttributePathElement pathElement : this.path) {
            if (current==null){//broken path
                return false;
            }
            current = pathElement.getNext(current);
        }
        if (current==null){//broken path
            return false;
        }
        DataStorageMetadata dataStorageMetadata =dictionary.getDataStorageMetadata(current.getDataClassName());
        if (dataStorageMetadata==null) {
            return false;
        }
        if (dataStorageMetadata.getAttribute(attribute)==null){
            return false;
        }
        return dataStorageMetadata.getAttribute(attribute).isRetyped();
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
