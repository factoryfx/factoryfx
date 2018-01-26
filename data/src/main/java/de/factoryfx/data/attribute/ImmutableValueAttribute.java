package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;

/** base class for Attributes with immutable value(for ChangeListener)*/
public abstract class ImmutableValueAttribute<T,A extends Attribute<T,A>> extends Attribute<T,A> {
    //    @JsonProperty
    protected T value;
    private final Class<T> dataType;

    public ImmutableValueAttribute(Class<T> dataType) {
        super();
        this.dataType=dataType;
    }

    @Override
    public boolean internal_mergeMatch(T value) {
        return Objects.equals(this.value, value);
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        //do nothing
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        if (listeners!=null){
            updateListeners(value);
        }
    }

    List<AttributeChangeListener<T,A>> listeners;

    protected void updateListeners(T value){
        if (listeners==null){
            return;
        }
        for (AttributeChangeListener<T,A> listener: listeners){
            listener.changed(this,value);
        }
    }

    protected boolean listenersEmpty(){
        if (listeners==null){
            return true;
        }
        return listeners.isEmpty();
    }

    //override to change copy e.g mutable value
    protected void copyTo(Attribute<T,A> copyAttribute){
        copyAttribute.set(get());
    }

    @Override
    public void internal_copyTo(A copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyTo(copyAttribute);
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        copyTo(copyAttribute);
    }

    @Override
    public void internal_addListener(AttributeChangeListener<T,A> listener) {
        if (listeners==null){
            listeners=new ArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void internal_removeListener(AttributeChangeListener<T,A> listener) {
        if (listeners==null){
            return;
        }
        for (AttributeChangeListener<T,A> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener || listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
            }
        }
    }

    @Override
    public String getDisplayText() {
        if (value!=null){
            return value.toString();
        }
        return "<empty>";
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(dataType);
    }

    @JsonValue
    protected T getValue() {
        return get();
    }

    @JsonValue
    protected void setValue(T value) {
        set(value);
    }

    /** alternative to equals on value, type-safe , less verbose, without worrying about hidden contracts
     * @param value compare value
     * @return true if equals
     */
    public boolean match(T value){
        if (get()!=null){
            return internal_mergeMatch(value);
        }
        return false;
    }

    /**see: {@link #match},
     * @param attribute compare attribute
     * @return true if equals
     */
    public boolean match(A attribute){
        return match(attribute.get());
    }

    public boolean internal_isUserReadOnly() {
        return userReadOnly;
    }

    private boolean userReadOnly=false;
    /**
     * marks the attribute as readonly for the user
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A userReadOnly(){
        userReadOnly=true;
        return (A)this;
    }
}
