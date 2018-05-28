package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends Data {
        public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute();

    }

    @Test
    public void test_merge(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.byteArrayAttribute.set(new byte[]{1,2,3,4});
        aTest1=aTest1.internal().prepareUsableCopy();

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4});
        aTest2=aTest1.internal().prepareUsableCopy();

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());

        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4,5});
        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertArrayEquals(new byte[]{1,2,3,4,5},aTest1.byteArrayAttribute.get());
    }


}