package de.factoryfx.data.storage.migration.datamigration;

import java.util.ArrayList;
import java.util.List;

public class PathBuilder<V>{
    private Class<V> valueClass;
    private final List<String> path= new ArrayList<>();

    public PathBuilder(Class<V> valueClass) {
        this.valueClass = valueClass;
    }

    public static <V> PathBuilder<V> value(Class<V> valueClass) {
        return new PathBuilder<>(valueClass);
    }

    public PathBuilder<V> pathElement(String pathElement) {
        path.add(pathElement);
        return this;
    }

    public AttributePath<V> attribute(String attribute) {
        path.add(attribute);
        return new AttributePath<>(valueClass,path);
    }



}
