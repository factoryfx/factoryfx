package de.factoryfx.data.merge.attribute;

import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class ViewMergeHelper<T> extends AttributeMergeHelper<T> {

    public ViewMergeHelper(Attribute<T> attribute) {
        super(attribute);
    }

    public boolean hasNoConflict(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        return true;
    }

}
