package de.factoryfx.data.attribute.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class WrappingValueAttribute<V> extends ImmutableValueAttribute<V,WrappingValueAttribute<V>> {
    private final Supplier<V> getFunction;
    private final Consumer<V> setFunction;

    public WrappingValueAttribute(Class<V> dataType, Supplier<V> getFunction, Consumer<V> setFunction) {
        super(dataType);
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
        updateListeners(value);
    }
}
