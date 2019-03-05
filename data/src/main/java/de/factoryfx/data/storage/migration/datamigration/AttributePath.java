package de.factoryfx.data.storage.migration.datamigration;


import java.util.ArrayList;
import java.util.List;

public class AttributePath<V> {
    private final List<String> path;
    private final Class<V> valueClass;

    public AttributePath(Class<V> valueClass, List<String> path) {
        this.valueClass = valueClass;
        this.path=path;
    }

    public V resolve(DataJsonNode node) {
        DataJsonNode current = node;

        List<String> path = new ArrayList<>(this.path);
        String attribute = path.remove(path.size() - 1);
        for (String pathElement : path) {
            current = current.getChild(pathElement);
        }
        return current.getAttributeValue(attribute, valueClass);
    }


}
