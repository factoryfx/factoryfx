package de.factoryfx.data.attribute;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ValueMapAttribute<K, V> extends ValueAttribute<ObservableMap<K,V>> implements Map<K,V> {
    private final Class<K> keyType;
    private final Class<V> valueType;

    public ValueMapAttribute(AttributeMetadata attributeMetadata, Class<K> keyType, Class<V> valueType) {
        super(attributeMetadata,null);
        this.keyType=keyType;
        this.valueType=valueType;
        set(FXCollections.observableMap(new TreeMap<>()));

        get().addListener((MapChangeListener<K, V>) change -> {
            for (AttributeChangeListener<ObservableMap<K,V>> listener: listeners){
                listener.changed(ValueMapAttribute.this,get());
            }
        });
    }

    @JsonCreator
    protected ValueMapAttribute() {
        this(null,null,null);
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get().entrySet(), item -> item.getKey()+":"+item.getValue()).getDisplayText();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableMap.class,keyType,valueType, AttributeTypeInfo.AttributeTypeCategory.MAP);
    }

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return get().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return get().get(key);
    }

    @Override
    public V put(K key, V value) {
        return get().put(key,value);
    }

    @Override
    public V remove(Object key) {
        return get().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        get().putAll(m);
    }

    @Override
    public void clear() {
        get().clear();
    }

    @Override
    public Set<K> keySet() {
        return get().keySet();
    }

    @Override
    public Collection<V> values() {
        return get().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return get().entrySet();
    }
}
