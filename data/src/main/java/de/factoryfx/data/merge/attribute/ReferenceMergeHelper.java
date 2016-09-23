package de.factoryfx.data.merge.attribute;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class ReferenceMergeHelper<T extends Data> extends AttributeMergeHelper<T> {

    public ReferenceMergeHelper(Attribute<T> attribute) {
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
