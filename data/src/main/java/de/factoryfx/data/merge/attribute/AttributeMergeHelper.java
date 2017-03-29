package de.factoryfx.data.merge.attribute;

import de.factoryfx.data.attribute.Attribute;

public class AttributeMergeHelper<T> {

    protected final Attribute<T> attribute;

    public AttributeMergeHelper(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    public void mergeTyped(T originalValue, T newValue) {
        attribute.set(newValue);
    }

    public boolean hasConflict(Attribute<?> originalAttribute, Attribute<?> newAttribute) {
        if (newAttribute.internal_match(originalAttribute)) {
            return false;
        }
        if (attribute.internal_match(originalAttribute)) {
            return false;
        }
        if (attribute.internal_match(newAttribute)) {
            return false;
        }
        return true;
    }

    /**
     * check if merge should be executed e.g. not if values ar equals
     * */
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Attribute<?> originalAttribute, Attribute<?> newAttribute) {
        if (!attribute.internal_match(originalAttribute) || attribute.internal_match(newAttribute)) {
            return false ;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void merge(Attribute<?> originalValue, Attribute<?> newValue) {
        mergeTyped((T) originalValue.get(), (T) newValue.get());
    }

}
