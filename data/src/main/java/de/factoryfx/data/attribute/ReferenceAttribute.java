package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;

public class ReferenceAttribute<T extends Data> extends ReferenceBaseAttribute<T,T,ReferenceAttribute<T>> {

    private T value;

    @JsonCreator
    protected ReferenceAttribute(T value) {
        super(null,(AttributeMetadata)null);
        set(value);
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(Class ,AttributeMetadata)
     * */
    public ReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(clazz,attributeMetadata);
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(AttributeMetadata,Class)
     * */
    @SuppressWarnings("unchecked")
    public ReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata,clazz);
    }

    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        if (get() != null) {
            get().internal().collectModelEntitiesTo(allModelEntities);
        }
    }

    @Override
    public Attribute<T> internal_copy() {
        final ReferenceAttribute<T> result = new ReferenceAttribute<>(containingFactoryClass, metadata);
        result.set(get());
        return result;
    }

    @Override
    public boolean internal_match(T value) {
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
        if (root!=null && value!=null && value.internal().getRoot()!=root) {
            value.internal().propagateRoot(root);
        }
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyAttribute.set((T) dataCopyProvider.apply(get()));
    }

    @Override
    public void internal_semanticCopyTo(Attribute<T> copyAttribute) {
        if (get()!=null){
            if (copySemantic==CopySemantic.SELF){
                copyAttribute.set(get());
            } else {
                copyAttribute.set(get().internal().semanticCopy());
            }
        }

    }

    private CopySemantic copySemantic = CopySemantic.COPY;

    /** @see Data.DataUtility#semanticCopy() */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A setCopySemantic(CopySemantic copySemantic){
        this.copySemantic=copySemantic;
        return (A)this;
    }

    @JsonValue
    T getValue() {
        return value;
    }

    @JsonValue
    void setValue(T value) {
        this.value = value;
    }

    List<AttributeChangeListener<T>> listeners= new ArrayList<>();
    @Override
    public void internal_addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener ||  listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
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
    public void internal_visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.reference(this);
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    public void internal_deleteFactory(){
        T removedFactory=get();
        set(null);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(removedFactory, root);
        }
    }

    public T internal_addNewFactory(){
        if (newValueProvider==null){
            try {
                set(containingFactoryClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            T newFactory = newValueProvider.apply(root);
            set(newFactory);
        }
        getOptional().ifPresent(data->data.internal().propagateRoot(root));
        return get();
    }

    public Class<T> internal_getReferenceClass(){
        return this.containingFactoryClass;
    }

}
