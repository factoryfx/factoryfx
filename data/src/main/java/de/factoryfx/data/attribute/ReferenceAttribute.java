package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.*;
import de.factoryfx.data.Data;

public abstract class ReferenceAttribute<T extends Data, A extends ReferenceBaseAttribute<T,T,A>> extends ReferenceBaseAttribute<T,T,A> {
    @JsonProperty("v")
    private T value;

    @JsonCreator
    protected ReferenceAttribute(T value) {
        super();
        set(value);
    }

    @JsonCreator
    protected ReferenceAttribute() {
        super();
    }

    @Override
    public boolean internal_mergeMatch(T value) {
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
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        Data currentReferenceContent = get();

        if (currentReferenceContent != null) {
            T value = (T) idToDataMap.get(currentReferenceContent.getId());
            if (get()!=value){
                set(value);
            }
        }
    }

    @Override
    public T get() {
        return value;
    }

    @JsonIgnore
    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    }


    @Override
    public void set(T value) {
        this.value=value;
        if (root!=null && value!=null) {
            value.internal().addBackReferencesForSubtree(root,this.parent);
        }
        if (listeners!=null) {
            for (AttributeChangeListener<T, A> listener : listeners) {
                listener.changed(this, value);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(A copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyAttribute.set((T) dataCopyProvider.apply(get()));
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        if (get()!=null){
            if (getCopySemantic()==CopySemantic.SELF){
                copyAttribute.set(get());
            } else {
                copyAttribute.set(get().internal().semanticCopy());
            }
        }
    }

//    @JsonUnwrapped
//    @JsonValue
    @JsonGetter
    @JsonMerge(OptBoolean.FALSE)
    protected T getValue() {
        return value;
    }

//    @JsonUnwrapped
//    @JsonValue
    @JsonSetter
    protected void setValue(T value) {
        this.value = value;
    }

    List<AttributeChangeListener<T,A>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<T,A> listener) {
        if (listeners==null){
            listeners= new ArrayList<>();
        }
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T,A> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<T,A> listenerItem: new ArrayList<>(listeners)){
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

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(internal_getReferenceClass(),null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    public void internal_deleteFactory(){
        T removedFactory=get();
        set(null);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(removedFactory, root);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> internal_createNewPossibleValues(){
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
        this.value=(T)newValue.get();//faster than call set, backreferences are updated anyway for all after merge
        if (listeners!=null) {
            for (AttributeChangeListener<T, A> listener : listeners) {
                listener.changed(this, value);
            }
        }
    }

}
