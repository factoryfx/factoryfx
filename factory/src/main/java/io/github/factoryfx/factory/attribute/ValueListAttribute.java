package io.github.factoryfx.factory.attribute;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ValueListAttribute<T, A extends Attribute<List<T>,A>> extends ImmutableValueAttribute<List<T>,A> implements List<T> {
    private final Class<T> itemType;

    public ValueListAttribute(Class<T> itemType) {
        super();
        this.itemType = itemType;
        this.value = new ArrayList<>();
    }

    public Class<T> internal_getItemType() {
        return itemType;
    }

    private void afterModify(){
        updateListeners(ValueListAttribute.this);
        if (this.root!=null){
            root.internal().addModified(this.parent);
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

    //** use to get the list for the liveobject. Unlike get() this method returns the delegate list which is faster in the liveobject because it does not have changedetection. */
    public List<T> instance() {
        return this.value;
    }

    @Override
    public List<T> get() {
        return this;
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){//workaround for jackson
            this.value.clear();
        } else {
            this.value.clear();
            this.addAll(value);
        }
    }

    private void beforeModify(){
        handleOriginalValue(this.value);
    }

    @Override
    protected void setOriginalValue(List<T> old) {
        this.originalValue=new ArrayList<>(old);
    }

    @Override
    protected void resetValue() {
        this.value.clear();
        this.value.addAll(originalValue);
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
        beforeModify();
        boolean add = this.value.add(t);
        afterModify();
        return add;
    }

    @Override
    public boolean remove(Object o) {
        beforeModify();
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
        beforeModify();
        boolean b = this.value.addAll(c);
        afterModify();
        return b;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        beforeModify();
        boolean b = this.value.addAll(index, c);
        afterModify();
        return b;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        beforeModify();
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
        beforeModify();
        this.value.clear();
        afterModify();
    }

    @Override
    public T get(int index) {
        return this.value.get(index);
    }

    @Override
    public T set(int index, T element) {
        beforeModify();
        T result = this.value.set(index, element);
        afterModify();
        return result;
    }

    @Override
    public void add(int index, T element) {
        beforeModify();
        this.value.add(index,element);
        afterModify();
    }

    @Override
    public T remove(int index) {
        beforeModify();
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
