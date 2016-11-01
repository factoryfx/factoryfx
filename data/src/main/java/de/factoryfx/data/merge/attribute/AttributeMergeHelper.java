package de.factoryfx.data.merge.attribute;

import java.util.Objects;
import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class AttributeMergeHelper<T> {

    protected final Attribute<T> attribute;

    public AttributeMergeHelper(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    protected boolean equal(T currentValueNeverNull, T newValueNeverNull) {
        return Objects.equals(currentValueNeverNull, newValueNeverNull);
    }


    public boolean equalValuesTyped(T value) {
        return equal(attribute.get(), value);
    }

    public void mergeTyped(Optional<T> originalValue, T newValue) {
        attribute.set(newValue);
    }

    /**to support AttributeMergeHelper that do nothing*/
    public boolean executeMerge(){
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean hasNoConflict(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        Optional<T> originalValueTyped = Optional.empty();
        if (originalValue.isPresent()) {
            originalValueTyped = Optional.ofNullable((T) originalValue.get().get());
        }
        Optional<T> newValueTyped = Optional.empty();
        if (newValue.isPresent()) {
            newValueTyped = Optional.ofNullable((T) newValue.get().get());
        }
        return hasNoConflictTyped(originalValueTyped, newValueTyped);
    }

    /**
     * check if merge should be executed e.g. not if values ar equals
     * */
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        T originalValueTyped = null;
        if (originalValue.isPresent()) {
            originalValueTyped = (T) originalValue.get().get();
        }
        T newValueTyped = null;
        if (newValue.isPresent()) {
            newValueTyped = (T)newValue.get().get();
        }

        if (!equalValuesTyped(originalValueTyped) || equalValuesTyped(newValueTyped)) {
            return false ;
        }

        return true;
    }


    public boolean hasNoConflictTyped(Optional<T> originalValue, Optional<T> newValue) {
        T currentFieldValue = attribute.get();
        T originalFieldValue = null;
        T newFieldValue = null;

        if (originalValue.isPresent()) {
            originalFieldValue = originalValue.get();
        }
        if (newValue.isPresent()) {
            newFieldValue = newValue.get();
        }

        if (equal(newFieldValue, originalFieldValue)) {
            return true;
        }
        if (equal(currentFieldValue, originalFieldValue)) {
            return true;
        }
        if (equal(currentFieldValue, newFieldValue)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void merge(Optional<Attribute<?>> originalValue, Attribute<?> newValue) {
        Optional<T> originalValueTyped = Optional.empty();
        if (originalValue.isPresent()) {
            originalValueTyped = Optional.ofNullable((T) originalValue.get().get());
        }
        mergeTyped(originalValueTyped, (T) newValue.get());
    }

}
