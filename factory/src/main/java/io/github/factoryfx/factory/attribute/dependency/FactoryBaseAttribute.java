package io.github.factoryfx.factory.attribute.dependency;

import com.fasterxml.jackson.annotation.*;

import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.AttributeMatch;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Consumer;

/**
 * Attribute with factory
 * @param <F> Factory type in the attribute
 * @param <A> Attribute self

 */
public class FactoryBaseAttribute<R extends FactoryBase<?,R>,L,F extends FactoryBase<? extends L,R>, A extends ReferenceBaseAttribute<R,F,F,A>> extends ReferenceBaseAttribute<R,F,F,A> {

    @JsonProperty("v")
    private F value;


    @Override
    public boolean internal_match(AttributeMatch<F> value) {
        return internal_referenceEquals(this.value,value.get());
    }


    @Override
    @SuppressWarnings("unchecked")
    public <RL extends FactoryBase<?,RL>> void internal_fixDuplicateObjects(Map<UUID, FactoryBase<?,RL>> idToDataMap) {
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
    public void set(F factory) {
        this.value=factory;
        if (root!=null) {
            root.internal().needRecalculationForBackReferences();
            if (factory!=null){
                factory.internal().setRootDeep(root);//intentionally just added flat, this compromise between performance an convenience, deep set would be too slow for that case addBackreference must be called manually
            }
        }
        updateListeners(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void internal_copyTo(AttributeCopy<F> copyAttribute, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {
        F factory = get();
        if (factory!=null) {
            F copy = (F) factory.internal().copyDeep(level, maxLevel, oldData, parent, root);
            copyAttribute.set(copy);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_semanticCopyTo(AttributeCopy<F> copyAttribute) {
        if (get()!=null){
            if (internal_getCopySemantic()== CopySemantic.SELF){
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

    @Override
    public void internal_merge(F newValue) {
        this.value=newValue;//faster than call set, backreferences are updated anyway for all after merge
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

    @Override
    public void internal_visitChildren(Consumer<FactoryBase<?, R>> consumer, boolean includeViews) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}
