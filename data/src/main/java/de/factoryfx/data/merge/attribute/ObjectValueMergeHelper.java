package de.factoryfx.data.merge.attribute;

import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.util.ObjectValueAttribute;

public class ObjectValueMergeHelper<T extends Data> extends AttributeMergeHelper<T> {
    @SuppressWarnings("unchecked") //TODO ObjectValueAttribute<T> generic fix
    public ObjectValueMergeHelper(ObjectValueAttribute attribute) {
        super(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        return false;
    }



}
