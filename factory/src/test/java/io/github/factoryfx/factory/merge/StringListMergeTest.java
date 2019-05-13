package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringListMergeTest extends MergeHelperTestBase {

    public static class StringListTest extends FactoryBase<Void,StringListTest> {
        public final StringListAttribute refB= new StringListAttribute();

    }


    @Test
    public void test_same(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");
        aTest1 = aTest1.internal().finalise();

        Assertions.assertEquals("11111", aTest1.refB.get(0));
        Assertions.assertEquals("222222", aTest1.refB.get(1));
        Assertions.assertEquals("33333", aTest1.refB.get(2));


        StringListTest update = new StringListTest();
        update.refB.add("11111");
        update.refB.add("222222");
        update.refB.add("33333");
        update = update.internal().finalise();

        Assertions.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());
        Assertions.assertEquals("11111", aTest1.refB.get(0));
        Assertions.assertEquals("222222", aTest1.refB.get(1));
        Assertions.assertEquals("33333", aTest1.refB.get(2));
    }

    @Test
    public void test_1_different(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");
        aTest1 = aTest1.internal().finalise();

        StringListTest update = new StringListTest();
        update.refB.add("11111");
        update.refB.add("222222");
        update.refB.add("33333qqqqq");
        update = update.internal().finalise();


        Assertions.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());
        Assertions.assertEquals("11111", aTest1.refB.get().get(0));
        Assertions.assertEquals("222222", aTest1.refB.get().get(1));
        Assertions.assertEquals("33333qqqqq", aTest1.refB.get().get(2));
    }

    @Test
    public void test_1_deleted(){
        StringListTest aTest1 = new StringListTest();
        aTest1.refB.add("11111");
        aTest1.refB.add("222222");
        aTest1.refB.add("33333");
        aTest1 = aTest1.internal().finalise();


        StringListTest update = new StringListTest();
        update.refB.add("11111");
        update.refB.add("222222");
        update = update.internal().finalise();

        Assertions.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());
        Assertions.assertEquals(2, aTest1.refB.get().size());
        Assertions.assertEquals("11111", aTest1.refB.get().get(0));
        Assertions.assertEquals("222222", aTest1.refB.get().get(1));
    }

}