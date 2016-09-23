package de.factoryfx.data.merge;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.util.StringListAttribute;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class StringListMergeTest extends MergeHelperTestBase {

    public static class StringListTest extends IdData {
        public final StringListAttribute refB= new StringListAttribute(new AttributeMetadata());

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