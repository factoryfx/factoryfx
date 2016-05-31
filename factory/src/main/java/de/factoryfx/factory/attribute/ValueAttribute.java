package de.factoryfx.factory.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.DataMergeHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;

public class ValueAttribute<T, U extends Property<T>> extends Attribute<T> {
    private final Supplier<U> observableFactory;
    //    @JsonProperty
    private T value;
    @JsonIgnore
    private U observable;
    public ValueAttribute(AttributeMetadata<T> attributeMetadata, Supplier<U> observableFactory) {
        super(attributeMetadata);
        this.observableFactory = observableFactory;
    }

    @Override
    public void collectChildren(Set<FactoryBase<?,?>> allModelEntities) {
        //nothing
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new DataMergeHelper<>(this);
    }

    @Override
    public T get() {
        return value;
    }


    Map<AttributeChangeListener<T>, InvalidationListener> listeners= new HashMap<>();
    @Override
    public void addListener(AttributeChangeListener<T> listener) {
        InvalidationListener invalidationListener = observable1 -> {
            listener.changed((T) observable1);
        };
        listeners.put(listener,invalidationListener);
        getObservable().addListener(invalidationListener);
    }
    @Override
    public void removeListener(AttributeChangeListener<T> listener) {
        getObservable().removeListener(listeners.get(listener));
        listeners.remove(listener);
    }



    protected U getObservable() {
        if (observable == null) {
            observable = observableFactory.get();
            observable.setValue(get());
            observable.addListener(observable1 -> {
                set(observable.getValue());
            });
        }
        return observable;
    }

    public void set(T value) {
        if (observable != null) {
            observable.setValue(value);
        }
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        this.value = (T) value;
    }

    @JsonValue
    T getValue() {
        return value;
    }

    @JsonValue
    void setValue(T value) {
        this.value = value;
    }
}
