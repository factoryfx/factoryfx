package de.factoryfx.factory.merge;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.ByteArrayAttribute;
import de.factoryfx.factory.util.VoidLiveObject;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends FactoryBase<VoidLiveObject,StringTestPojo> {
        public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute(new AttributeMetadata());

        @Override
        protected VoidLiveObject createImp(Optional<VoidLiveObject> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void test_merge(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.byteArrayAttribute.set(new byte[]{1,2,3,4});

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4});

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());

        aTest2.byteArrayAttribute.set(new byte[]{1,2,3,4,5});
        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertArrayEquals(new byte[]{1,2,3,4,5},aTest1.byteArrayAttribute.get());
    }


}