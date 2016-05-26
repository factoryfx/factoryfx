package de.factoryfx.factory.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class MapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>, SimpleMapProperty<K,V>> {

    public MapAttribute(AttributeMetadata<ObservableMap<K, V>> attributeMetadata) {
        super(attributeMetadata, () -> new SimpleMapProperty<>());
        set(FXCollections.observableMap(new TreeMap<>()));
    }

    public MapAttribute(AttributeMetadata<ObservableMap<K, V>> attributeMetadata, Map<K, V> defaultValue) {
        this(attributeMetadata);
        get().putAll(defaultValue);
    }

    @JsonCreator
    public MapAttribute(ObservableMapJacksonAbleWrapper<K, V> map) {
        this((AttributeMetadata<ObservableMap<K, V>>)null);
        this.set(map.unwrap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        ObservableMap<K, V> newMap = (ObservableMap<K, V>) value;
        set(newMap);
    }

    public V get(String key) {
        return get().get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return get().getOrDefault(key, defaultValue);
    }


    Map<InvalidationListener, MapChangeListener<K, V>> listeners= new HashMap<>();
    @Override
    public void addListener(InvalidationListener listener) {
        MapChangeListener<K, V> mapListener = change -> listener.invalidated(get());
        listeners.put(listener,mapListener);
        getObservable().addListener(mapListener);
    }
    @Override
    public void removeListener(InvalidationListener listener) {
        getObservable().removeListener(listeners.get(listener));
    }

}
