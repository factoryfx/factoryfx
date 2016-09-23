package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.ReferenceMergeHelper;

public class ReferenceAttribute<T extends Data> extends Attribute<T> {

    private T value;
    private Optional<Class<T>> clazz;

    @JsonCreator
    protected ReferenceAttribute(T value) {
        super(null);
        set(value);
    }

    public ReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=Optional.ofNullable(clazz);
    }

    @SuppressWarnings("unchecked")
    //workaround for generic parameter ReferenceAttribute<Example<V>> webGuiResource=new ReferenceAttribute(Example<V>)
    public ReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata);
        this.clazz=Optional.ofNullable(clazz);
    }

    @Override
    public void collectChildren(Set<Data> allModelEntities) {
        if (get() != null) {
            get().collectModelEntitiesTo(allModelEntities);
        }
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new ReferenceMergeHelper<>(this);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        Data currentReferenceContent = get();

        if (currentReferenceContent != null) {
            currentReferenceContent.fixDuplicateObjects(getCurrentEntity);
            Optional<Data> existingOptional = getCurrentEntity.apply(currentReferenceContent.getId());
            if (existingOptional.isPresent()) {
                set((T) existingOptional.get());
            }
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
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
        this.value=value;
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
    public void addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void removeListener(AttributeChangeListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public String getDisplayText() {
        String referenceDisplayText = "empty";
        if (value!=null){
            referenceDisplayText=value.getDisplayText();
        }
        return referenceDisplayText;
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.reference(this);
    }

    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(ReferenceAttribute.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    public Optional<Function<Data,List<T>>> possibleValueProviderFromRoot=Optional.empty();
    public Optional<Function<Data,T>> newValueProviderFromRoot=Optional.empty();

    public void addNewFactory(Data root){
        newValueProviderFromRoot.ifPresent(newFactoryFunction -> {
            T newFactory = newFactoryFunction.apply(root);
            set(newFactory);
        });
        if (!newValueProviderFromRoot.isPresent()){
            if (clazz.isPresent()){
                try {
                    set(clazz.get().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                set(null);
            }

        }
    }

    @SuppressWarnings("unchecked")
    public List<T> possibleValues(Data root){
        ArrayList<T> result = new ArrayList<>();
        possibleValueProviderFromRoot.ifPresent(factoryBaseListFunction -> {
            List<T> factories = factoryBaseListFunction.apply(root);
            factories.forEach(factory -> result.add(factory));
        });
        if (!newValueProviderFromRoot.isPresent()){
            if (!possibleValueProviderFromRoot.isPresent()){
                for (Data factory: root.collectChildrenDeep()){
                    if (clazz.isPresent()){
                        if (clazz.get().isAssignableFrom(factory.getClass())){
                            result.add((T) factory);
                        }
                    }
                }
            }
        }
        return result;
    }

}
