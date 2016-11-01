package de.factoryfx.data.merge.attribute;

import de.factoryfx.data.Data;

//** AttributeMergeHelper does nothing*/
public class NopMergeHelper<T extends Data> extends AttributeMergeHelper<T> {

    public NopMergeHelper() {
        super(null);
    }

    @Override
    public boolean executeMerge() {
        return false;
    }
}
