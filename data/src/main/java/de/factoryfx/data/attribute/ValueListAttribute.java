package de.factoryfx.data.attribute;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ValueListAttribute<T> extends ValueAttribute<List<T>> implements Collection<T> {
    private final Class<T> itemType;
    private final ObservableList<T> observableValue;

    public ValueListAttribute(Class<T> itemType, AttributeMetadata attributeMetadata) {
        super(attributeMetadata,null);
        this.itemType=itemType;
        observableValue = FXCollections.observableArrayList();
        value=observableValue;

        observableValue.addListener((ListChangeListener<T>) c -> {
            for (AttributeChangeListener<List<T>> listener: listeners){
                listener.changed(ValueListAttribute.this,get());
            }
        });
    }

    @Override
    public Attribute<List<T>> internal_copy() {
        final ValueListAttribute<T> result = new ValueListAttribute<>(itemType, metadata);
        result.addAll(get());
        return result;
    }

    @JsonCreator
    protected ValueListAttribute() {
        this(null,null);
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), t -> t.toString()).getDisplayText();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION);
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){//workaround for jackson
            observableValue.clear();
        } else {
            observableValue.setAll(value);
        }
    }

    public List<T> filtered(Predicate<T> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
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
    public boolean removeAll(Collection<?> c) {
        return get().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return get().retainAll(c);
    }

    @Override
    public void clear() {
        get().clear();
    }


}
