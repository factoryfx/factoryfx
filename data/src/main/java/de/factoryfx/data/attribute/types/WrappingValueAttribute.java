package de.factoryfx.data.attribute.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class WrappingValueAttribute<V> extends ValueAttribute<V> {
    private final Supplier<V> getFunction;
    private final Consumer<V> setFunction;

    public WrappingValueAttribute(AttributeMetadata attributeMetadata, Class<V> dataType, Supplier<V> getFunction, Consumer<V> setFunction) {
        super(attributeMetadata, dataType);
        this.getFunction = getFunction;
        this.setFunction = setFunction;
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
