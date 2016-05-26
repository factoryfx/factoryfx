package de.factoryfx.model.merge.attribute;

import java.util.Objects;
import java.util.Optional;

import de.factoryfx.model.attribute.Attribute;

//Leaf dat e.g. strings
public class DataMergeHelper<T> extends AttributeMergeHelper<T> {

    private final Attribute<T> attribute;

    public DataMergeHelper(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    boolean equal(T currentValueNeverNull, T newValueNeverNull) {
        return Objects.equals(currentValueNeverNull, newValueNeverNull);
    }

    private boolean equalNullFix(T currentValue, T newValue) {
        if (currentValue == null && newValue == null) {
            return true;
        }
        if (currentValue == null || newValue == null) {
            return false;
        }
        return equal(currentValue, newValue);
    }

    @Override
    public boolean equalValuesTyped(T value) {
        return equal(attribute.get(), value);
    }

    @Override
    public boolean isMergeableTyped(Optional<T> originalValue, Optional<T> newValue) {
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

    @Override
    public void mergeTyped(Optional<T> originalValue, T newValue) {
        T originalFieldValue = null;
        T currentFieldValue = attribute.get();
        if (originalValue.isPresent()) {
            originalFieldValue = originalValue.get();
        }

        if (equal(currentFieldValue, originalFieldValue)) {
            attribute.set(newValue);
        }
    }

}
