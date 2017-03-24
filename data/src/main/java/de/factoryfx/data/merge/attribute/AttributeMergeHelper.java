package de.factoryfx.data.merge.attribute;

import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class AttributeMergeHelper<T> {

    protected final Attribute<T> attribute;

    public AttributeMergeHelper(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    public void mergeTyped(Optional<T> originalValue, T newValue) {
        attribute.set(newValue);
    }

    @SuppressWarnings("unchecked")
    public boolean hasNoConflict(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        Optional<Attribute<T>> originalValueTyped = Optional.empty();
        if (originalValue.isPresent()) {
            originalValueTyped = Optional.of((Attribute<T>) originalValue.get());
        }
        Optional<Attribute<T>> newValueTyped = Optional.empty();
        if (newValue.isPresent()) {
            newValueTyped = Optional.of((Attribute<T>) newValue.get());
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

        if (!attribute.internal_match(originalValueTyped) || attribute.internal_match(newValueTyped)) {
            return false ;
        }

        return true;
    }


    private boolean hasNoConflictTyped(Optional<Attribute<T>> originalValue, Optional<Attribute<T>> newValue) {
        T originalFieldValue = null;
        T newFieldValue = null;

        if (originalValue.isPresent()) {
            originalFieldValue = originalValue.get().get();
        }
        if (newValue.isPresent()) {
            newFieldValue = newValue.get().get();
        }

        if (newFieldValue == originalFieldValue)
            return true;

        if (newValue.isPresent() &&  newValue.get().internal_match(originalFieldValue)) {
            return true;
        }
        if (attribute.internal_match(originalFieldValue)) {
            return true;
        }
        if (attribute.internal_match(newFieldValue)) {
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
