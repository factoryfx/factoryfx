package de.factoryfx.factory.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableMapJacksonAbleWrapper;
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


    Map<AttributeChangeListener<ObservableMap<K,V>>, MapChangeListener<K, V>> listeners= new HashMap<>();
    @Override
    public void addListener(AttributeChangeListener<ObservableMap<K,V>> listener) {
        MapChangeListener<K, V> mapListener = change -> listener.changed(get());
        listeners.put(listener,mapListener);
        getObservable().addListener(mapListener);
    }
    @Override
    public void removeListener(AttributeChangeListener<ObservableMap<K,V>> listener) {
        getObservable().removeListener(listeners.get(listener));
        listeners.remove(listener);
    }

}
