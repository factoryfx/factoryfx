package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.factoryfx.data.ChangeAble;

public class ValueListAttribute<T, A extends Attribute<List<T>,A>> extends ImmutableValueAttribute<List<T>,A> implements List<T>, ChangeAble {
    private final Class<T> itemType;

    public ValueListAttribute(Class<T> itemType) {
        super(null);
        this.itemType = itemType;
        this.value = new ArrayList<>();
    }

    public void afterModify(){
        if (listeners!=null) {
            for (AttributeChangeListener<List<T>, A> listener : listeners) {
                listener.changed(ValueListAttribute.this, this.value);
            }
        }
    }

    @JsonCreator
    protected ValueListAttribute() {
        this(null);
    }

    @JsonIgnore
    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(this.value, Object::toString).getDisplayText();
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(Set.class,null,null,itemType, AttributeTypeInfo.AttributeTypeCategory.COLLECTION);
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){//workaround for jackson
            this.value.clear();
        } else {
            this.value.clear();
            this.value.addAll(value);
        }
    }

    public List<T> filtered(Predicate<T> predicate) {
        return this.value.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.value.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.value.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.value.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean add = this.value.add(t);
        afterModify();
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = this.value.remove(o);
        afterModify();
        return remove;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean b = this.value.addAll(c);
        afterModify();
        return b;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean b = this.value.addAll(index, c);
        afterModify();
        return b;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean b = this.value.removeAll(c);
        afterModify();
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.value.retainAll(c);
    }

    @Override
    public void clear() {
        this.value.clear();
        afterModify();
    }

    @Override
    public T get(int index) {
        return this.value.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.value.set(index,element);
    }

    @Override
    public void add(int index, T element) {
        this.value.add(index,element);
        afterModify();
    }

    @Override
    public T remove(int index) {
        T remove = this.value.remove(index);
        afterModify();
        return remove;
    }

    @Override
    public int indexOf(Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.value.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.value.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.value.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.value.subList(fromIndex,toIndex);
    }

}
