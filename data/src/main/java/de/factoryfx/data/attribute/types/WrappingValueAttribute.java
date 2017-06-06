package de.factoryfx.data.attribute.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class WrappingValueAttribute<V> extends ImmutableValueAttribute<V> {
    private final Supplier<V> getFunction;
    private final Consumer<V> setFunction;
    private final Class<V> dataType;

    public WrappingValueAttribute(AttributeMetadata attributeMetadata, Class<V> dataType, Supplier<V> getFunction, Consumer<V> setFunction) {
        super(attributeMetadata, dataType);
        this.getFunction = getFunction;
        this.setFunction = setFunction;
        this.dataType = dataType;
    }

    @Override
    protected Attribute<V> createNewEmptyInstance() {
        return new WrappingValueAttribute<>(metadata,dataType,getFunction,setFunction);
    }

    @Override
    public V get() {
        return getFunction.get();
    }

    @Override
    public void set(V value) {
        setFunction.accept(value);
        for (AttributeChangeListener<V> listener: listeners){
            listener.changed(this,value);
        }
    }
}
