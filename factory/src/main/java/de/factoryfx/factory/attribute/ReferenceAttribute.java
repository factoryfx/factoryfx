package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.ReferenceMergeHelper;

public class ReferenceAttribute<L extends LiveObject,T extends FactoryBase<L,? super T>> extends Attribute<T,ReferenceAttribute<L,T>> {

    private T value;
    private Class<T> clazz;

    @JsonCreator
    ReferenceAttribute(T value) {
        super(null);
        set(value);
    }

    public ReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=clazz;
    }

    @SuppressWarnings("unchecked")
    //workaround for generic parameter ReferenceAttribute<Example<V>> webGuiResource=new ReferenceAttribute(Example<V>)
    public ReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata);
        this.clazz=clazz;
    }

    @Override
    public void collectChildren(Set<FactoryBase<?,?>> allModelEntities) {
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
    public void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity) {
        FactoryBase currentReferenceContent = get();

        if (currentReferenceContent != null) {
            currentReferenceContent.fixDuplicateObjects(getCurrentEntity);
            Optional<FactoryBase<?,?>> existingOptional = getCurrentEntity.apply(currentReferenceContent.getId());
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
        return new AttributeTypeInfo(null,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    public Optional<Function<FactoryBase<?,?>,List<T>>> possibleValueProviderFromRoot=Optional.empty();
    public Optional<Function<FactoryBase<?,?>,T>> newValueProviderFromRoot=Optional.empty();

    public void addNewFactory(FactoryBase<?,?> root){
        newValueProviderFromRoot.ifPresent(newFactoryFunction -> {
            T newFactory = newFactoryFunction.apply(root);
            set(newFactory);
        });
        if (!newValueProviderFromRoot.isPresent()){
            try {
                set(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> possibleValues(FactoryBase<?,?> root){
        ArrayList<T> result = new ArrayList<>();
        possibleValueProviderFromRoot.ifPresent(factoryBaseListFunction -> {
            List<T> factories = factoryBaseListFunction.apply(root);
            factories.forEach(factory -> result.add(factory));
        });
        if (!newValueProviderFromRoot.isPresent()){
            if (!possibleValueProviderFromRoot.isPresent()){
                for (FactoryBase<?,?> factory: root.collectChildFactories()){
                    if (clazz.isAssignableFrom(factory.getClass())){
                        result.add((T) factory);
                    }
                }
            }
        }
        return result;
    }

    public L instance(){
        if (get()==null){
            return null;
        }
        return get().instance();
    }
}
