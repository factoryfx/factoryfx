package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.DataMergeHelper;

public class ValueAttribute<T> extends Attribute<T> {
    //    @JsonProperty
    private T value;

    public ValueAttribute(AttributeMetadata<T> attributeMetadata) {
        super(attributeMetadata);
    }

    @Override
    public void collectChildren(Set<FactoryBase<?,?>> allModelEntities) {
        //nothing
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new DataMergeHelper<>(this);
    }

    @Override
    public void fixDuplicateObjects(Function<String, Optional<FactoryBase<?, ?>>> getCurrentEntity) {
        //do nothing
    }

    @Override
    public T get() {
        return value;
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
        return metadata.displayName+": "+ value;
    }

    public void set(T value) {
        this.value = value;
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        this.value = (T) value;
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
