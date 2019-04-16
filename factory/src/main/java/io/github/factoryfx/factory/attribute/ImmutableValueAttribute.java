package io.github.factoryfx.factory.attribute;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.validation.ValidationResult;

/** base class for Attributes with immutable value(for ChangeListener)*/
//@JsonDeserialize(using = JsonNullAwareDeserializer.class)
public abstract class ImmutableValueAttribute<T,A extends Attribute<T,A>> extends Attribute<T,A> {
    //    @JsonProperty
    @JsonProperty("v")
    protected T value;

    public ImmutableValueAttribute() {
        super();
    }

    @Override
    public boolean internal_mergeMatch(T value) {
        return Objects.equals(this.value, value);
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

    private AttributeChangeListener<T,A> listener;//performance optimization if only one listener
    private List<AttributeChangeListener<T,A>> listeners;

    protected void updateListeners(T value){
        if (listenersEmpty()){
            return;
        }
        if (listener!=null){
            listener.changed(this,value);
        } else {
            for (AttributeChangeListener<T, A> listener : listeners) {
                listener.changed(this, value);
            }
        }
    }

    protected boolean listenersEmpty(){
        return listener==null && (listeners==null || listeners.isEmpty());
    }

    public List<AttributeChangeListener<T,A>> internal_getListeners(){
        if (listener!=null) {
            return List.of(listener);
        }
        if (listeners!=null) {
            return listeners;
        }
        return Collections.emptyList();
    }

    @Override
    public void internal_copyTo(A copyAttribute) {
        copyTo(copyAttribute);
    }

    //override to change copy e.g mutable value
    protected void copyTo(Attribute<T,A> copyAttribute){
        copyAttribute.set(get());
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        copyTo(copyAttribute);
    }

    @Override
    public void internal_addListener(AttributeChangeListener<T,A> newListener) {
        if (listener==null){
            this.listener=newListener;
        } else {
            if (this.listeners == null) {
                this.listeners = new ArrayList<>();
                this.listeners.add(this.listener);
                this.listener = null;
            } else {
                listeners.add(newListener);
            }
        }
    }

    @Override
    public void internal_removeListener(AttributeChangeListener<T,A> removeListener) {
        if (listeners==null && listener==null){
            return;
        }
        if (this.listener.unwrap()==removeListener || this.listener.unwrap()==null){
            this.listener=null;
            return;
        }
        for (AttributeChangeListener<T,A> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==removeListener || listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
            }
        }
    }

    @JsonIgnore
    @Override
    public String getDisplayText() {
        if (value!=null){
            return value.toString();
        }
        return "<empty>";
    }

//    @JsonUnwrapped
    @JsonGetter
//    @JsonValue
    protected T getValue() {
        return get();
    }

//    @JsonUnwrapped
    @JsonSetter
//    @JsonMerge
//    @JsonValue
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

    @Override
    public boolean internal_required() {
        return !nullable;
    }

    private static final Validation requiredValidation = value -> {
        boolean error = value == null;
        if (value instanceof String){
            if (((String)value).isEmpty()){
                error=true;
            }
        }
        return new ValidationResult(error, new LanguageText().en("required parameter").de("Pflichtparameter"));
    };

    private boolean nullable;

    @SuppressWarnings("unchecked")
    public A nullable(){
        nullable=true;
        return (A)this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ValidationError> internal_validate(FactoryBase<?,?> parent, String attributeVariableName) {
        if (!nullable){
            this.validation(requiredValidation);// to minimise object creations
        }
        return super.internal_validate(parent, attributeVariableName);
    }

    @SuppressWarnings("unchecked")
    public A defaultValue(T defaultValue) {
        set(defaultValue);
        return (A)this;
    }
}
