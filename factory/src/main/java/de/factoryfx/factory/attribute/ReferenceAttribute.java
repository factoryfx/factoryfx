package de.factoryfx.factory.attribute;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.ReferenceMergeHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ReferenceAttribute<T extends FactoryBase<?,? super T>> extends Attribute<T> {

    private T value;
    @JsonIgnore
    private SimpleObjectProperty<T> observable;

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

    private Property<T> getObservable() {
        if (observable == null) {
            observable = new SimpleObjectProperty<>();
            observable.setValue(get());
            observable.addListener(observable1 -> {
                set(observable.getValue());
            });
        }
        return observable;
    }

    @Override
    public void set(T value) {
        if (observable != null) {
            observable.setValue(value);
        }
        this.value=value;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        getObservable().addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        getObservable().removeListener(listener);
    }

    @JsonValue
    T getValue() {
        return value;
    }

    @JsonValue
    void setValue(T value) {
        this.value = value;
    }
}
