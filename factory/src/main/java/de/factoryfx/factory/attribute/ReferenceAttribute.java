package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.ReferenceMergeHelper;

public class ReferenceAttribute<T extends FactoryBase<?,? super T>> extends Attribute<T> {

    private T value;

    public ReferenceAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata);
    }

    public ReferenceAttribute(AttributeMetadata<T> attributeMetadata, T defaultValue) {
        this(attributeMetadata);
        set(defaultValue);
    }

    @JsonCreator
    public ReferenceAttribute(T value) {
        this(new AttributeMetadata<>(""));
        set(value);
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
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        if (value != null) {
            set((T) ((FactoryBase<?,?>) value).reconstructMetadataDeep(objectPool));
        }
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
            listener.changed(value);
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
}
