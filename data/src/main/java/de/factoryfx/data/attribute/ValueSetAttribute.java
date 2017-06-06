package de.factoryfx.data.attribute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class ValueSetAttribute<T> extends ImmutableValueAttribute<Set<T>> implements Set<T> {
    private final Class<T> itemType;

    public ValueSetAttribute(Class<T> itemType, AttributeMetadata attributeMetadata) {
        super(attributeMetadata,null);
        this.itemType = itemType;
        final ObservableSet<T> observableSet = FXCollections.observableSet(new HashSet<>());
        value= observableSet;

        observableSet.addListener((SetChangeListener<T>) change -> {
            for (AttributeChangeListener<Set<T>> listener: listeners){
                listener.changed(ValueSetAttribute.this,get());
            }
        });
    }

    @Override
    protected Attribute<Set<T>> createNewEmptyInstance() {
        return new ValueSetAttribute<>(itemType, metadata);
    }

    @JsonCreator
    protected ValueSetAttribute() {
        this(null,null);
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), item -> item.toString()).getDisplayText();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableSet.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION);
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(Set<T> value) {
        this.get().clear();
        this.get().addAll(value);
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
    public boolean contains(Object o) {
        return get().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return get().iterator();
    }

    @Override
    public Object[] toArray() {
        return get().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return get().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return get().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return get().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return get().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return get().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return get().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return get().removeAll(c);
    }

    @Override
    public void clear() {
        get().clear();
    }
}
