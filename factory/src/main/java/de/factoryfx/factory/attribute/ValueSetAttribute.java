package de.factoryfx.factory.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableSetJacksonAbleWrapper;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ValueSetAttribute<T> extends ValueAttribute<ObservableSet<T>, SimpleSetProperty<T>> {

    public ValueSetAttribute(AttributeMetadata<ObservableSet<T>> attributeMetadata) {
        super(attributeMetadata, ()->new SimpleSetProperty<>());
        set(FXCollections.observableSet(new HashSet<>()));
    }


    @JsonCreator
    public ValueSetAttribute(ObservableSetJacksonAbleWrapper<T> setCollection) {
        this((AttributeMetadata<ObservableSet<T>>)null);
        set(setCollection.unwrap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        set((ObservableSet<T>) value);
    }

    public void add(T value) {
        get().add(value);
    }

    public void addAll(Collection<T> values) {
        get().addAll(values);
    }

    public boolean contains(T value) {
        return get().contains(value);
    }

    public int size() {
        return get().size();
    }

    public Stream<T> stream() {
        return get().stream();
    }

    public T[] toArray(T[] a) {
        return get().toArray(a);
    }

    public Object[] toArray() {
        return get().toArray();
    }

    Map<AttributeChangeListener<ObservableSet<T>>, SetChangeListener<T>> listeners= new HashMap<>();
    @Override
    public void addListener(AttributeChangeListener<ObservableSet<T>> listener) {
        SetChangeListener<T> setListener = change -> listener.changed(get());
        listeners.put(listener,setListener);
        getObservable().addListener(setListener);
    }
    @Override
    public void removeListener(AttributeChangeListener<ObservableSet<T>> listener) {
        getObservable().removeListener(listeners.get(listener));
        listeners.remove(listener);
    }
}
