package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.ByteArrayAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ByteArrayMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends FactoryBase<Void, StringTestPojo> {
        public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute();

    }

    @Test
    public void test_merge(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.byteArrayAttribute.set(new byte[]{1,2,3,4});
        aTest1=aTest1.internal().addBackReferences();

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4});
        aTest2=aTest1.internal().addBackReferences();

        Assertions.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());

        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4,5});
        Assertions.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assertions.assertArrayEquals(new byte[]{1,2,3,4,5},aTest1.byteArrayAttribute.get());
    }


}