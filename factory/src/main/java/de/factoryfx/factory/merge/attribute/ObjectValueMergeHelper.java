package de.factoryfx.factory.merge.attribute;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;

public class ObjectValueMergeHelper<T extends FactoryBase<?,? super T>> extends AttributeMergeHelper<T> {
    @SuppressWarnings("unchecked") //TODO ObjectValueAttribute<T> generic fix
    public ObjectValueMergeHelper(ObjectValueAttribute attribute) {
        super(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?,?>> originalValue, Optional<Attribute<?,?>> newValue) {
        return false;
    }



}
