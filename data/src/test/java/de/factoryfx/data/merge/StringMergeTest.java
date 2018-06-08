package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import org.junit.Assert;
import org.junit.Test;

public class StringMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends Data {
        public final StringAttribute stringA=new StringAttribute();
        public final StringAttribute stringB=new StringAttribute();
    }

    @Test
    public void test_merge_same(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");
        aTest1 = aTest1.internal().addBackReferences();

        StringTestPojo update = new StringTestPojo();
        update.stringA.set("11111111");
        update = update.internal().addBackReferences();

        Assert.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());

        update.stringA.set("11111111qqqqq");
        Assert.assertTrue(merge(aTest1, aTest1, update).hasNoConflicts());
        Assert.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");
        aTest1 = aTest1.internal().addBackReferences();

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.stringA.set("11111111qqqqq");
        aTest2 = aTest2.internal().addBackReferences();

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change_2(){
        StringTestPojo current = new StringTestPojo();
        current.stringA.set("11111111xxxxxxx");
        current.stringB.set("11111111");
        current = current.internal().addBackReferences();

        StringTestPojo original = new StringTestPojo();
        original.stringA.set("11111111");
        original.stringB.set("11111111");
        original = original.internal().addBackReferences();

        StringTestPojo newData = new StringTestPojo();
        newData.stringA.set("11111111");
        newData.stringB.set("11111111qqqqq");
        newData = newData.internal().addBackReferences();

        Assert.assertTrue(merge(current, original, newData).hasNoConflicts());
        Assert.assertEquals("11111111xxxxxxx", current.stringA.get());
        Assert.assertEquals("11111111qqqqq", current.stringB.get());
    }

}