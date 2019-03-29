package io.github.factoryfx.factory.storage.migration.datamigration;



import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.Map;

public class AttributePath<V> {
    private final List<AttributePathElement> path;
    private final Class<V> valueClass;
    private final String attribute;

    public AttributePath(Class<V> valueClass, List<AttributePathElement> path, String attribute) {
        this.valueClass = valueClass;
        this.path = path;
        this.attribute = attribute;
    }

    public V resolveAttributeValue(DataJsonNode root, SimpleObjectMapper simpleObjectMapper) {
        DataJsonNode current = root;

        for (AttributePathElement pathElement : this.path) {
            current = pathElement.getNext(current);
        }
        if (FactoryBase.class.isAssignableFrom(valueClass) && current.getAttributeValue(attribute)!=null && current.getAttributeValue(attribute).isTextual()) {
            String id= current.getAttributeIdValue(attribute);
            Map<String, DataJsonNode> idToDataJsonNodeMap = root.collectChildrenMapFromRoot();
            if (!idToDataJsonNodeMap.containsKey(id)) {
                throw new IllegalStateException("can't find id: "+id);
            }
            return idToDataJsonNodeMap.get(id).asData(valueClass, simpleObjectMapper);
        } else {
            return current.getAttributeValue(attribute, valueClass, simpleObjectMapper);
        }
    }

    public boolean isPathToRemovedAttribute(DataStorageMetadataDictionary dictionary) {
        DataStorageMetadata current = dictionary.getRootDataStorageMetadata();

        for (AttributePathElement pathElement : this.path) {
            current = pathElement.getNext(current,dictionary);
        }
        if (current.getAttribute(attribute)==null){
            return false;
        }
        return current.getAttribute(attribute).isRemoved();
    }


    public boolean match(AttributePath<V> path) {
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
