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
    public boolean internal_mergeMatch(AttributeMatch<T> value) {
        return Objects.equals(this.value, value.get());
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        updateListeners(value);
    }

    @Override
    public void internal_copyTo(AttributeCopy<T> copyAttribute, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {
        copyAttribute.set(get());
    }

    @Override
    public void internal_semanticCopyTo(AttributeCopy<T> copyAttribute) {
        copyAttribute.set(get());
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

    @JsonIgnore
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

    @Override
    public void internal_reset() {
        internal_removeAllListener();
    }

    @Override
    public void internal_addBackReferences(FactoryBase<?, ?> root, FactoryBase<?, ?> parent) {
        //nothing
    }

    public Optional<T> getNullable(){
        return Optional.ofNullable(get());
    }
}
