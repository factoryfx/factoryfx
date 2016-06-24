package de.factoryfx.factory.merge.attribute;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.Attribute;

public class ReferenceMergeHelper<T extends FactoryBase<?,? super T>> extends AttributeMergeHelper<T> {

    public ReferenceMergeHelper(Attribute<T,?> attribute) {
        super(attribute);
    }

    @Override
    protected  boolean equal(T refContent1, T refContent2) {
        if (refContent1 == null && refContent2 == null) {
            return true;
        }
        if (refContent1 == null || refContent2 == null) {
            return false;
        }
        return refContent1.getId().equals(refContent2.getId());
    }


}
