package de.factoryfx.factory.attribute;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<ObservableList<T>> {


    public ValueListAttribute() {
        set(FXCollections.observableArrayList() );

        get().addListener((ListChangeListener<T>) c -> {
            for (AttributeChangeListener<ObservableList<T>> listener: listeners){
                listener.changed(ValueListAttribute.this,get());
            }
        });
    }

    @JsonCreator
    public ValueListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        this();
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

    @Override
    public String getDisplayText(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ get().size()+")\n");
        for (T item:  get()){
            stringBuilder.append(item);
            stringBuilder.append(",\n");
        }
        return metadata.labelText.getPreferred(locale)+":\n"+stringBuilder.toString();
    }

}
