package de.factoryfx.factory.merge;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

public class StringMergeTest extends MergeHelperTestBase {

    public static class StringTestPojo extends FactoryBase<ExampleLiveObjectA,StringTestPojo> {
        public final StringAttribute stringA=new StringAttribute();
        public final StringAttribute stringB=new StringAttribute();

        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void test_merge_same(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.stringA.set("11111111");

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());

        aTest2.stringA.set("11111111qqqqq");
        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change(){
        StringTestPojo aTest1 = new StringTestPojo();
        aTest1.stringA.set("11111111");

        StringTestPojo aTest2 = new StringTestPojo();
        aTest2.stringA.set("11111111qqqqq");

        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals("11111111qqqqq",aTest1.stringA.get());
    }

    @Test
    public void test_merge_change_2(){
        StringTestPojo current = new StringTestPojo();
        current.stringA.set("11111111xxxxxxx");
        current.stringB.set("11111111");

        StringTestPojo original = new StringTestPojo();
        original.stringA.set("11111111");
        original.stringB.set("11111111");

        StringTestPojo newData = new StringTestPojo();
        newData.stringA.set("11111111");
        newData.stringB.set("11111111qqqqq");

        Assert.assertTrue(merge(current, original, newData).hasNoConflicts());
        Assert.assertEquals("11111111xxxxxxx", current.stringA.get());
        Assert.assertEquals("11111111qqqqq", current.stringB.get());
    }

}