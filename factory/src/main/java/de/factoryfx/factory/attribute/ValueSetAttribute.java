package de.factoryfx.factory.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableSetJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ValueSetAttribute<T> extends ValueAttribute<ObservableSet<T>> {

    public ValueSetAttribute(AttributeMetadata<ObservableSet<T>> attributeMetadata) {
        super(attributeMetadata);
        set(FXCollections.observableSet(new HashSet<>()));

        get().addListener((SetChangeListener<T>) change -> {
            for (AttributeChangeListener<ObservableSet<T>> listener: listeners){
                listener.changed(ValueSetAttribute.this,get());
            }
        });
    }


    @JsonCreator
    public ValueSetAttribute(ObservableSetJacksonAbleWrapper<T> setCollection) {
        this((AttributeMetadata<ObservableSet<T>>)null);
        set(setCollection.unwrap());
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

    @Override
    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder("Set (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return metadata.displayName+":\n"+stringBuilder.toString();
    }
}
