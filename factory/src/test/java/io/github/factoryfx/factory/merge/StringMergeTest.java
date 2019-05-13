package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends FactoryBase<Void, StringTestPojo> {
        public final StringAttribute stringA=new StringAttribute();
        public final StringAttribute stringB=new StringAttribute();
    }

    @Test
    public void test_merge_same(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");
        aTest1 = aTest1.internal().finalise();

        StringTestPojo update = new StringTestPojo();
        update.stringA.set("11111111");
        update = update.internal().finalise();

        Assertions.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());

        update.stringA.set("11111111qqqqq");
        Assertions.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());
        Assertions.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");
        aTest1 = aTest1.internal().finalise();

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.stringA.set("11111111qqqqq");
        aTest2 = aTest2.internal().finalise();

        Assertions.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assertions.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change_2(){
        StringTestPojo current = new StringTestPojo();
        current.stringA.set("11111111xxxxxxx");
        current.stringB.set("11111111");
        current = current.internal().finalise();

        StringTestPojo original = new StringTestPojo();
        original.stringA.set("11111111");
        original.stringB.set("11111111");
        original = original.internal().finalise();

        StringTestPojo newData = new StringTestPojo();
        newData.stringA.set("11111111");
        newData.stringB.set("11111111qqqqq");
        newData = newData.internal().finalise();

        Assertions.assertTrue(merge(current, original, newData).hasNoConflicts());
        Assertions.assertEquals("11111111xxxxxxx", current.stringA.get());
        Assertions.assertEquals("11111111qqqqq", current.stringB.get());
    }

}