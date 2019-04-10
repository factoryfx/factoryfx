package io.github.factoryfx.factory.attribute.dependency;

import com.fasterxml.jackson.annotation.*;

import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;

import java.util.*;

/**
 * Attribute with factory
 * @param <F> Factory type in the attribute
 * @param <A> Attribute self

 */
public class FactoryBaseAttribute<R extends FactoryBase<?,R>,L,F extends FactoryBase<? extends L,R>, A extends ReferenceBaseAttribute<R,F,F,A>> extends ReferenceBaseAttribute<R,F,F,A> {

    @JsonProperty("v")
    private F value;


    @Override
    public boolean internal_mergeMatch(F value) {
        if (this.value == null && value == null) {
            return true;
        }
        if (this.value == null || value == null) {
            return false;
        }
        return this.value.idEquals(value);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<String, FactoryBase<?,?>> idToDataMap) {
        FactoryBase<?,?> currentReferenceContent = get();

        if (currentReferenceContent != null) {
            F value = (F) idToDataMap.get(currentReferenceContent.getId());
            if (get()!=value){
                set(value);
            }
        }
    }

    @Override
    public F get() {
        return value;
    }

    @JsonIgnore
    public Optional<F> getOptional() {
        return Optional.ofNullable(value);
    }


    @Override
    public void set(F value) {
        this.value=value;
        if (root!=null && value!=null) {
            value.internal().addBackReferencesForSubtreeUnsafe(root,this.parent);
        }
        if (listeners!=null) {
            for (AttributeChangeListener<F, A> listener : listeners) {
                listener.changed(this, value);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(A copyAttribute,final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root) {
        F factory = get();
        if (factory!=null) {
            copyAttribute.set((F)factory.internal().copyDeep(level, maxLevel, oldData, parent, root));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_semanticCopyTo(A copyAttribute) {
        if (get()!=null){
            if (getCopySemantic()== CopySemantic.SELF){
                copyAttribute.set(get());
            } else {
                copyAttribute.set((F)get().utility().semanticCopy());
            }
        }
    }

    //    @JsonUnwrapped
//    @JsonValue
    @JsonGetter
    @JsonMerge(OptBoolean.FALSE)
    protected F getValue() {
        return value;
    }

    //    @JsonUnwrapped
//    @JsonValue
    @JsonSetter
    protected void setValue(F value) {
        this.value = value;
    }

    List<AttributeChangeListener<F,A>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<F,A> listener) {
        if (listeners==null){
            listeners= new ArrayList<>();
        }
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<F,A> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<F,A> listenerItem: new ArrayList<>(listeners)){
                if (listenerItem.unwrap()==listener ||  listenerItem.unwrap()==null){
                    listeners.remove(listenerItem);
                }
            }
        }
    }

    @JsonIgnore
    @Override
    public String getDisplayText() {
        String referenceDisplayText = "empty";
        if (value!=null){
            referenceDisplayText=value.internal().getDisplayText();
        }
        return referenceDisplayText;
    }

    public void internal_deleteFactory(){
        F removedFactory=get();
        set(null);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(removedFactory, root);
        }
    }

    @SuppressWarnings("unchecked")
    public List<F> internal_createNewPossibleValues(){
        FactoryTreeBuilderBasedAttributeSetup factoryTreeBuilderBasedAttributeSetup = root.internal().getFactoryTreeBuilderBasedAttributeSetup();
        if (factoryTreeBuilderBasedAttributeSetup!=null){
            return factoryTreeBuilderBasedAttributeSetup.createNewFactory(clazz);
        }
        if (newValuesProviderFromRootAndAttribute!=null) {
            return newValuesProviderFromRootAndAttribute.apply(root, (A)this);
        }
        if (getNewValueProvider()!=null) {
            return Collections.singletonList(getNewValueProvider().apply(root));
        }
        return new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_merge(Attribute<?,?> newValue) {
        this.value=(F)newValue.get();//faster than call set, backreferences are updated anyway for all after merge
        if (listeners!=null) {
            for (AttributeChangeListener<F, A> listener : listeners) {
                listener.changed(this, value);
            }
        }
    }


    public FactoryBaseAttribute() {
        super();
    }


    public L instance(){
        if (get()==null){
            return null;
        }
        return get().internal().instance();
    }

//    @SuppressWarnings("unchecked")
//    public <LO> LO instance(){
//        if (get()==null){
//            return null;
//        }
//        return (LO)get().internalFactory().instance();
//    }

    @Override
    public boolean internal_required() {
        return !nullable;
    }

    private static final Validation requiredValidation = value -> {
        boolean error = value == null;
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
    public List<ValidationError> internal_validate(FactoryBase<?,?> parent,String attributeVariableName) {
        if (!nullable){
            this.validation(requiredValidation);// to minimise object creations
        }
        return super.internal_validate(parent,attributeVariableName);
    }
}
