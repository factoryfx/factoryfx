package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.Function;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;

public abstract class ReferenceAttribute<T extends Data, A extends ReferenceBaseAttribute<T,T,A>> extends ReferenceBaseAttribute<T,T,A> {
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
        return this.value.getId().equals(value.getId());
    }


    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        Data currentReferenceContent = get();

        if (currentReferenceContent != null) {
            set((T)idToDataMap.get(currentReferenceContent.getId()));
        }
    }

    @Override
    public T get() {
        return value;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    }


    @Override
    public void set(T value) {
        this.value=value;
        if (root!=null && value!=null) {
            value.internal().propagateRootAndParent(root,this.parent);
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
            if (copySemantic==CopySemantic.SELF){
                copyAttribute.set(get());
            } else {
                copyAttribute.set(get().internal().semanticCopy());
            }
        }

    }

    @JsonValue
    T getValue() {
        return value;
    }

    @JsonValue
    void setValue(T value) {
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

    public boolean internal_hasCustomNewValuesProvider(){
        return newValuesProvider!=null;
    }

    public List<T> internal_createNewPossibleValues(){
        if (newValuesProvider!=null) {
            return newValuesProvider.apply(root);
        }
        if (getNewValueProvider()!=null) {
            return Collections.singletonList(getNewValueProvider().apply(root));
        }
        return new ArrayList<>();
    }

    boolean defaultValueUsed;
    @SuppressWarnings("unchecked")
    public A defaultValue(T defaultValue) {
        defaultValueUsed=true;
        return super.defaultValue(defaultValue);
    }
    //**attributes with a default value can not be set to null(deleted)*/
    public boolean internal_isUserDeletable(){
        return !defaultValueUsed;
    }


    /**default values can be confusing in merge and history view.
     * a Fixed a id solves that, the
     * @param defaultValue  defaultValue
     * @param id unique id, usually a uuid
     * @return self
     * */
    @SuppressWarnings("unchecked")
    public A defaultValue(T defaultValue, String id) {
        set(defaultValue);
        get().setId(id);
        return (A)this;
    }

}
