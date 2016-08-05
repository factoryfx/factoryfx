package de.factoryfx.factory.merge;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.StringListAttribute;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

public class StringListMergeTest extends MergeHelperTestBase {

    public static class StringListTest extends FactoryBase<ExampleLiveObjectA,StringListTest> {
        public final StringListAttribute refB= new StringListAttribute(new AttributeMetadata());

        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
            return null;
        }
    }


    @Test
    public void test_same(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");


        StringListTest aTest2 = new StringListTest();
        aTest2.refB.add("11111");
        aTest2.refB.add("222222");
        aTest2.refB.add("33333");

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("11111", aTest1.refB.get().get(0));
        Assert.assertEquals("222222", aTest1.refB.get().get(1));
        Assert.assertEquals("33333", aTest1.refB.get().get(2));
    }

    @Test
    public void test_1_different(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");


        StringListTest aTest2 = new StringListTest();
        aTest2.refB.add("11111");
        aTest2.refB.add("222222");
        aTest2.refB.add("33333qqqqq");


        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("11111", aTest1.refB.get().get(0));
        Assert.assertEquals("222222", aTest1.refB.get().get(1));
        Assert.assertEquals("33333qqqqq", aTest1.refB.get().get(2));
    }

    @Test
    public void test_1_deleted(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");


        StringListTest aTest2 = new StringListTest();
        aTest2.refB.add("11111");
        aTest2.refB.add("222222");

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals(2, aTest1.refB.get().size());
        Assert.assertEquals("11111", aTest1.refB.get().get(0));
        Assert.assertEquals("222222", aTest1.refB.get().get(1));
    }

}