package de.factoryfx.model.merge.attribute;

import java.util.Optional;

import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.Attribute;

public class ReferenceMergeHelper<T extends FactoryBase<?,? super T>> extends AttributeMergeHelper<T> {
    final Attribute<T> attribute;

    public ReferenceMergeHelper(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean equalValuesTyped(T value) {
        if (value == null && attribute.get() == null) {
            return true;
        }
        if (value == null || attribute.get() == null) {
            return false;
        }
        return (value).getId().equals(attribute.get().getId());
    }

    @Override
    public boolean isMergeableTyped(Optional<T> originalValue, Optional<T> newValue) {
        T currentReferenceContent = attribute.get();
        T newReferenceContent = null;
        if (newValue.isPresent()) {
            newReferenceContent = newValue.get();
        }
        T originalReferenceContent = null;
        if (originalValue.isPresent()) {
            originalReferenceContent = originalValue.get();
        }

        if (isEquals(currentReferenceContent, originalReferenceContent)) {
            return true;
        }
        if (isEquals(newReferenceContent, originalReferenceContent)) {
            return true;
        }
        return false;
    }

    @Override
    public void mergeTyped(Optional<T> originalValue, T newValue) {
        T currentReferenceContent = attribute.get();
        T newReferenceContent = newValue;

        if (newReferenceContent == null) {
            attribute.set(null);
        } else {
            if (currentReferenceContent == null || !currentReferenceContent.getId().equals(newReferenceContent.getId())) {
                attribute.set(newReferenceContent);
            }
        }
    }

    private boolean isEquals(T refContent1, T refContent2) {
        if (refContent1 == null && refContent2 == null) {
            return true;
        }
        if (refContent1 == null || refContent2 == null) {
            return false;
        }
        return refContent1.getId().equals(refContent2.getId());
    }

}
