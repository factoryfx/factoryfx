package de.factoryfx.data.merge.attribute;

import java.util.Arrays;

import de.factoryfx.data.attribute.util.ByteArrayAttribute;

public class ByteArrayMergeHelper extends AttributeMergeHelper<byte[]> {

    public ByteArrayMergeHelper(ByteArrayAttribute attribute) {
        super(attribute);
    }

    protected boolean equal(byte[] currentValueNeverNull, byte[] newValueNeverNull) {
        return Arrays.equals(currentValueNeverNull, newValueNeverNull);
    }
}
