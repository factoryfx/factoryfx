package de.factoryfx.data.storage.migration.datamigration;


import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.ArrayList;
import java.util.List;

public class AttributePath<V> {
    private final List<String> path;
    private final Class<V> valueClass;

    public AttributePath(Class<V> valueClass, List<String> path) {
        this.valueClass = valueClass;
        this.path=path;
    }

    public V resolve(DataJsonNode root) {
        DataJsonNode current = root;

        List<String> path = new ArrayList<>(this.path);
        String attribute = path.remove(path.size() - 1);
        for (String pathElement : path) {
            current = current.getChild(pathElement);
        }
        if (Data.class.isAssignableFrom(valueClass) && current.getAttributeValue(attribute)!=null && current.getAttributeValue(attribute).isTextual()) {
            String id= current.getAttributeIdValue(attribute);
            for (DataJsonNode dataJsonNode : root.collectChildrenFromRoot()) { //TODO optimize performance, maybe IdToDataJsonNode Map
                if (id.equals(dataJsonNode.getId())){
                    return dataJsonNode.asData(valueClass);
                }
            }
            throw new IllegalStateException("can't find id: "+id);
        } else {
            return current.getAttributeValue(attribute, valueClass);
        }
    }

    public boolean isPathToRemovedAttribute(DataStorageMetadataDictionary dictionary) {
        DataStorageMetadata current = dictionary.getRootDataStorageMetadata();

        List<String> path = new ArrayList<>(this.path);
        String attribute = path.remove(path.size() - 1);
        for (String pathElement : path) {
            current = current.getChild(pathElement,dictionary);
        }
        return current.getAttribute(attribute).isRemoved();
    }


}
