package de.factoryfx.data.attribute;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class ValueSetAttribute<T,A extends Attribute<Set<T>,A>> extends ImmutableValueAttribute<Set<T>,A> implements Set<T> {
    private final Class<T> itemType;

    public ValueSetAttribute(Class<T> itemType) {
        super(null);
        this.itemType = itemType;
        final ObservableSet<T> observableSet = FXCollections.observableSet(new HashSet<>());
        value= observableSet;

        observableSet.addListener((SetChangeListener<T>) change -> {
            updateListeners(get());
        });
    }

    @JsonCreator
    protected ValueSetAttribute() {
        this(null);
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), Object::toString).getDisplayText();
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
