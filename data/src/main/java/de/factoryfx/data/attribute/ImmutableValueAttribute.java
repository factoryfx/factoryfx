package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.*;
import de.factoryfx.data.Data;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.data.validation.ValidationResult;

/** base class for Attributes with immutable value(for ChangeListener)*/
//@JsonDeserialize(using = JsonNullAwareDeserializer.class)
public abstract class ImmutableValueAttribute<T,A extends Attribute<T,A>> extends Attribute<T,A> {
    //    @JsonProperty
    @JsonProperty("v")
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

    @JsonIgnore
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
    public List<ValidationError> internal_validate(Data parent,String attributeVariableName) {
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
