package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;

public class ValueAttribute<T> extends Attribute<T> {
    //    @JsonProperty
    protected T value;
    private final Class<T> dataType;

    public ValueAttribute(AttributeMetadata attributeMetadata, Class<T> dataType) {
        super(attributeMetadata);
        this.dataType=dataType;
    }


    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        //nothing
    }

    @Override
    public boolean internal_match(T value) {
        return Objects.equals(this.value, value);
    }

    @Override
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new AttributeMergeHelper<>(this);
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        //do nothing
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    //override to change copy e.g mutable value
    protected void copyTo(Attribute<T> copyAttribute){
        copyAttribute.set(get());
    }

    @Override
    public void internal_copyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyTo(copyAttribute);
    }

    @Override
    public void internal_semanticCopyTo(Attribute<T> copyAttribute) {
        copyTo(copyAttribute);
    }

    protected final List<AttributeChangeListener<T>> listeners= new ArrayList<>();
    @Override
    public void internal_addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void internal_removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener || listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
            }
        }
    }

    @Override
    public String getDisplayText() {
        if (value!=null){
            return value.toString();
        }
        return "<empty>";
    }

    @Override
    public void internal_visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.value(this);
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(dataType);
    }

    @JsonValue
    protected T getValue() {
        return get();
    }

    @JsonValue
    protected void setValue(T value) {
        set(value);
    }


}
