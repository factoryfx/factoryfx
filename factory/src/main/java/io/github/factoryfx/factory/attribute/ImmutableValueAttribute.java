package io.github.factoryfx.factory.attribute;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.*;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.validation.ValidationResult;

/** base class for Attributes with immutable value(for ChangeListener)*/
//@JsonDeserialize(using = JsonNullAwareDeserializer.class)
@JsonInclude()//include null attributes
public abstract class ImmutableValueAttribute<T,A extends Attribute<T,A>> extends Attribute<T,A> {
    //    @JsonProperty
    @JsonProperty("v")
    protected T value;

    @JsonIgnore
    protected T originalValue;
    @JsonIgnore
    private boolean originalValueSet;

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
        T old=this.value;
        this.value = value;
        updateListeners(value);
        if (this.root!=null){
            this.root.internal().addModified(parent);
            handleOriginalValue(old);
        }
    }

    protected void handleOriginalValue(T old){
        if (!originalValueSet  && root!=null){
            originalValueSet=true;
            setOriginalValue(old);
        }
    }

    protected void resetValue(){
        value=originalValue;
    }

    protected void setOriginalValue(T old){
        originalValue=old;
    }

    @Override
    public void internal_resetModification() {
        if (originalValueSet) {
            resetValue();
        }
    }

    @Override
    public void internal_clearModifyState() {
        originalValue=null;
        originalValueSet=false;
    }

    @Override
    public void internal_copyTo(AttributeCopy<T> copyAttribute, Function<FactoryBase<?,?>,FactoryBase<?,?>> newCopyInstanceProvider, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {
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


    /**
     * this method is deprecated, instead default values should be set with the {@link io.github.factoryfx.factory.builder.FactoryTreeBuilder}
     * @param defaultValue defaultValue
     * @return attribute
     */
    @SuppressWarnings("unchecked")
    public A defaultValue(T defaultValue) {
        set(defaultValue);
        return (A)this;
    }

    @Override
    public void internal_reset() {
        internal_removeAllListener();
    }

    protected FactoryBase<?, ?> root;
    protected FactoryBase<?, ?> parent;
    @Override
    public void internal_addBackReferences(FactoryBase<?, ?> root, FactoryBase<?, ?> parent) {
        this.root = root;
        this.parent = parent;
    }

    public FactoryBase<?, ?> internal_getRoot() {
        return this.root;
    }

    public Optional<T> getNullable(){
        return Optional.ofNullable(get());
    }
}
