package de.factoryfx.process;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;

public class SubscriptionParameter extends ProcessParameter {
    public IntegerAttribute value = new IntegerAttribute();

    @Override
    public boolean isExecutable() {
        return true;
    }
}
