package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;

public class ValueAttribute<T> extends Attribute<T> {
    //    @JsonProperty
    private T value;
    private Class<T> dataType;

    public ValueAttribute(AttributeMetadata attributeMetadata, Class<T> dataType) {
        super(attributeMetadata);
        this.dataType=dataType;
    }

    @Override
    public void collectChildren(Set<Data> allModelEntities) {
        //nothing
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new AttributeMergeHelper<>(this);
    }

    @Override
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
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
        if (value!=null){
            return value.toString();
        }
        return "<empty>";
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.value(this);
    }

    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(dataType);
    }

    @Override
    public void set(T value) {
        this.value = value;
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    @JsonValue
    protected T getValue() {
        return value;
    }

    @JsonValue
    protected void setValue(T value) {
        this.value = value;
    }




}
