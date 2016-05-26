package de.factoryfx.factory.merge.attribute;

import java.util.Optional;

import de.factoryfx.factory.attribute.Attribute;

public abstract class AttributeMergeHelper<T> {

    protected AttributeMergeHelper() {
    }

    @SuppressWarnings("unchecked")
    public boolean equalValues(Attribute<?> value) {
        return equalValuesTyped((T) value.get());
    }

    abstract protected boolean equalValuesTyped(T value);

    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        Optional<T> originalValueTyped = Optional.empty();
        if (originalValue.isPresent()) {
            originalValueTyped = Optional.ofNullable((T) originalValue.get().get());
        }
        Optional<T> newValueTyped = Optional.empty();
        if (newValue.isPresent()) {
            newValueTyped = Optional.ofNullable((T) newValue.get().get());
        }
        return isMergeableTyped(originalValueTyped, newValueTyped);
    }

    abstract protected boolean isMergeableTyped(Optional<T> originalValue, Optional<T> newValue);

    @SuppressWarnings("unchecked")
    public void merge(Optional<Attribute<?>> originalValue, Attribute<?> newValue) {
        Optional<T> originalValueTyped = Optional.empty();
        if (originalValue.isPresent()) {
            originalValueTyped = Optional.ofNullable((T) originalValue.get().get());
        }
        mergeTyped(originalValueTyped, (T) newValue.get());
    }

    abstract protected void mergeTyped(Optional<T> originalValue, T newValue);
}
