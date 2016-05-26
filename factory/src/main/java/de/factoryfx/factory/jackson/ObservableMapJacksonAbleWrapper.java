package de.factoryfx.factory.jackson;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class ObservableMapJacksonAbleWrapper<T, E> implements ObservableMap<T, E> {

    private final ObservableMap<T, E> observableMap;

    public ObservableMapJacksonAbleWrapper() {
        this.observableMap = FXCollections.observableMap(new TreeMap<>());
    }

    public ObservableMapJacksonAbleWrapper(ObservableMap<T, E> observableMap) {
        this.observableMap = observableMap;
    }

    @Override
    public void addListener(MapChangeListener<? super T, ? super E> listener) {
        observableMap.addListener(listener);
    }

    @Override
    public void removeListener(MapChangeListener<? super T, ? super E> listener) {
        observableMap.removeListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableMap.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableMap.removeListener(listener);
    }

    @Override
    public int hashCode() {
        return observableMap.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return observableMap.equals(o);
    }

    @Override
    public int size() {
        return observableMap.size();
    }

    @Override
    public boolean isEmpty() {
        return observableMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return observableMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return observableMap.containsValue(value);
    }

    @Override
    public E get(Object key) {
        return observableMap.get(key);
    }

    @Override
    public E put(T key, E value) {
        return observableMap.put(key, value);
    }

    @Override
    public E remove(Object key) {
        return observableMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends T, ? extends E> m) {
        observableMap.putAll(m);
    }

    @Override
    public void clear() {
        observableMap.clear();
    }

    @Override
    public Set<T> keySet() {
        return observableMap.keySet();
    }

    @Override
    public Collection<E> values() {
        return observableMap.values();
    }

    @Override
    public Set<Entry<T, E>> entrySet() {
        return observableMap.entrySet();
    }

    public ObservableMap<T, E> unwrap() {
        return observableMap;
    }
}
