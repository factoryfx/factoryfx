package de.factoryfx.factory.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<ObservableList<T>, SimpleListProperty<T>> {


    public ValueListAttribute(AttributeMetadata<ObservableList<T>> attributeMetadata) {
        super(attributeMetadata,()->new SimpleListProperty<>());
        set(FXCollections.observableArrayList() );
    }

    @JsonCreator
    public ValueListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        this((AttributeMetadata<ObservableList<T>>)null);
        set(list.unwrap());
    }

    public void add(T value) {
        get().add(value);
    }

    public void addAll(Collection<T> values) {
        get().addAll(values);
    }

    public void addAll(T[] values) {
        get().addAll(values);
    }

    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        set((ObservableList<T>) value);
    }

    public boolean contains(T value) {
        return get().contains(value);
    }

    public T get(int i) {
        return get().get(i);
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


    Map<InvalidationListener, ListChangeListener<T>> listeners= new HashMap<>();
    @Override
    public void addListener(InvalidationListener listener) {
        ListChangeListener<T> mapListener = change -> listener.invalidated(get());
        listeners.put(listener,mapListener);
        getObservable().addListener(mapListener);
    }
    @Override
    public void removeListener(InvalidationListener listener) {
        getObservable().removeListener(listeners.get(listener));
    }

}
