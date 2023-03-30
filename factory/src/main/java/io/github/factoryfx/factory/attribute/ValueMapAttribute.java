package io.github.factoryfx.factory.attribute;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;


//TODO remove ObservableMap same as list
public abstract class ValueMapAttribute<K, V, A extends ValueMapAttribute<K,V,A>> extends ImmutableValueAttribute<Map<K,V>,A> implements Map<K,V> {
    private final Class<K> keyType;
    private final Class<V> valueType;

    public ValueMapAttribute(Class<K> keyType, Class<V> valueType) {
        super();
        this.keyType=keyType;
        this.valueType=valueType;
        this.value=new HashMap<>();
    }


    @JsonCreator
    protected ValueMapAttribute() {
        this(null,null);
    }

    @JsonIgnore
    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(value.entrySet(), item -> item.getKey()+":"+item.getValue()).getDisplayText();
    }

//    @Override
//    public Map<K,V> get() {
//        return this;
//    }

    private void afterModify(){
        updateListeners(ValueMapAttribute.this);
        if (this.root!=null){
            root.internal().addModified(this.parent);
        }
    }


    private void beforeModify(){
        handleOriginalValue(this.value);
    }

    @Override
    protected void setOriginalValue(Map<K,V> old) {
        this.originalValue=new HashMap<>(old);
    }

    @Override
    protected void resetValue() {
        this.value.clear();
        this.value.putAll(originalValue);
    }

    @Override
    public void internal_resetModification() {
        if (originalValue!=null) {
            this.value.clear();
            this.value.putAll(originalValue);
        }
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
    public boolean containsKey(Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.value.get(key);
    }

    @Override
    public V put(K key, V value) {
        beforeModify();
        V put = this.value.put(key, value);
        afterModify();
        return put;
    }

    @Override
    public V remove(Object key) {
        beforeModify();
        V remove = this.value.remove(key);
        afterModify();
        return remove;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        beforeModify();
        this.value.putAll(m);
        afterModify();
    }

    @Override
    public void clear() {
        beforeModify();
        this.value.clear();
        afterModify();
    }

    @Override
    public Set<K> keySet() {
        return this.value.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.value.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.value.entrySet();
    }

    //** use to get the list for the liveobject. */
    public Map<K,V> instance() {
        return this.value;
    }
}
