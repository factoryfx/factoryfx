package io.github.factoryfx.factory.attribute;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;


public abstract class ValueSetAttribute<T,A extends Attribute<Set<T>,A>> extends ImmutableValueAttribute<Set<T>,A> implements Set<T> {
    private final Class<T> itemType;

    public ValueSetAttribute(Class<T> itemType) {
        super();
        this.itemType = itemType;
        this.value= new HashSet<>();
    }

    @JsonCreator
    protected ValueSetAttribute() {
        this(null);
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(this.value, Object::toString).getDisplayText();
    }

    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(Set<T> value) {
        this.value.clear();
        this.addAll(value);
    }


    //FIXME useful but breaks jackson, try to find workaround
//    @JsonIgnore
//    @Override
//    public Set<T> get() {
//        return this;
//    }

    private void afterModify(){
        this.updateListeners(ValueSetAttribute.this);
        if (this.root!=null){
            root.internal().addModified(this.parent);
        }
    }

    private void beforeModify(){
        handleOriginalValue(this.value);
    }

    @Override
    protected void setOriginalValue(Set<T> old) {
        this.originalValue=new HashSet<>(old);
    }

    @Override
    protected void resetValue() {
        this.value.clear();
        this.value.addAll(originalValue);
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
        boolean b = value.addAll(c);
        afterModify();
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.value.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        beforeModify();
        boolean b = this.value.removeAll(c);
        afterModify();
        return b;
    }

    @Override
    public void clear() {
        beforeModify();
        this.value.clear();
        afterModify();
    }

    //** use to get the set for the liveobject. Unlike get() this method returns the delegate list which is faster in the liveobject because it does not have changedetection. */
    public Set<T> instance() {
        return this.value;
    }
}
