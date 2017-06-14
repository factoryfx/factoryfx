package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;

/** base class for Attributes with immutable value(for Changelistener)*/
public abstract class ImmutableValueAttribute<T,A extends Attribute<T,A>> extends Attribute<T,A> {
    //    @JsonProperty
    protected T value;
    private final Class<T> dataType;

    public ImmutableValueAttribute(Class<T> dataType) {
        super();
        this.dataType=dataType;
    }


    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        //nothing
    }

    @Override
    @SuppressWarnings("unchecked")
    public A internal_copy() {
        try {
            A result = createNewEmptyInstance();
            result.takeContentFromAttribute((A)this);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** new instance with same metadata but empty/no value*/
    protected abstract A createNewEmptyInstance();

    @Override
    public boolean internal_match(T value) {
        return Objects.equals(this.value, value);
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
        for (AttributeChangeListener<T,A> listener: listeners){
            listener.changed(this,value);
        }
    }

    //override to change copy e.g mutable value
    protected void copyTo(Attribute<T,A> copyAttribute){
        copyAttribute.set(get());
    }

    @Override
    public void internal_copyTo(A copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyTo(copyAttribute);
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        copyTo(copyAttribute);
    }

    protected final List<AttributeChangeListener<T,A>> listeners= new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void internal_addListener(AttributeChangeListener<T,A> listener) {
        listeners.add((AttributeChangeListener<T, A>) listener);
    }

    @Override
    public void internal_removeListener(AttributeChangeListener<T,A> listener) {
        for (AttributeChangeListener<T,A> listenerItem: new ArrayList<>(listeners)){
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

    @Override
    public void writeValueToJsonWrapper(AttributeJsonWrapper attributeJsonWrapper) {
        attributeJsonWrapper.value=get();
    }
}
