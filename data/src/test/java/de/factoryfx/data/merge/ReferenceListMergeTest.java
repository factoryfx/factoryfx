package de.factoryfx.data.merge;

import java.util.stream.Collectors;

import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ReferenceListMergeTest extends MergeHelperTestBase{

    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        newValue1.stringAttribute.set("1111");
        ExampleDataB newValue2 = new ExampleDataB();
        newValue2.stringAttribute.set("2222");
        ExampleDataB newValue3 = new ExampleDataB();
        newValue3.stringAttribute.set("2222");
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleDataA aTest2 = new ExampleDataA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);
        aTest2.referenceListAttribute.add(newValue3);

        Assert.assertTrue(merge(current, current, aTest2).hasNoConflicts());
        Assert.assertEquals(3, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1.stringAttribute.get(), current.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals(newValue2.stringAttribute.get(), current.referenceListAttribute.get(1).stringAttribute.get());
        Assert.assertEquals(newValue3.stringAttribute.get(), current.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Test
    public void test_1_new_addedt(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleDataA aTest2 = new ExampleDataA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);
        ExampleDataB replacedValue = new ExampleDataB();
        aTest2.referenceListAttribute.add(replacedValue);

        Assert.assertTrue(merge(current, current, aTest2).hasNoConflicts());
        Assert.assertEquals(3, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1, current.referenceListAttribute.get(0));
        Assert.assertEquals(newValue2, current.referenceListAttribute.get(1));
        Assert.assertEquals(replacedValue, current.referenceListAttribute.get(2));
    }

    @Test
    public void test_1_current_added(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);

        ExampleDataA orginal = current.internal().copy();
        ExampleDataB replacedValue = new ExampleDataB();
        current.referenceListAttribute.add(replacedValue);

        ExampleDataA aTest2 = new ExampleDataA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);

        Assert.assertTrue(merge(current, orginal, aTest2).hasNoConflicts());
        Assert.assertEquals(3, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1, current.referenceListAttribute.get(0));
        Assert.assertEquals(newValue2, current.referenceListAttribute.get(1));
        Assert.assertEquals(replacedValue, current.referenceListAttribute.get(2));
    }

    @Test
    public void test_delete(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleDataA aTest2 = new ExampleDataA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);

        Assert.assertTrue(merge(current, current, aTest2).hasNoConflicts());
        Assert.assertEquals(2, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1, current.referenceListAttribute.get(0));
        Assert.assertEquals(newValue2, current.referenceListAttribute.get(1));
    }

    @Ignore
    @Test
    public void test_both_added(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);


        ExampleDataA orginal = current.internal().copy();
        ExampleDataA update = current.internal().copy();

        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue3);

        ExampleDataB newValue4 = new ExampleDataB();
        update.referenceListAttribute.add(newValue4);

        Assert.assertTrue(merge(current, orginal, update).hasNoConflicts());
        Assert.assertEquals(4, current.referenceListAttribute.size());
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue1.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue2.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue3.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue4.getId()));
    }

    @Test
    public void test_no_change_should_merge_nothing(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);


        ExampleDataA orginal = current.internal().copy();
        ExampleDataA update = current.internal().copy();

        final MergeDiffInfo merge = merge(current, orginal, update);
        Assert.assertTrue(merge.hasNoConflicts());
        Assert.assertEquals(2, current.referenceListAttribute.size());
        Assert.assertEquals(0, merge.mergeInfos.size());
    }

    @Test
    public void test_order_changed(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);

        ExampleDataA orginal = current.internal().copy();

        ExampleDataA update = current.internal().copy();
        final ExampleDataB first = update.referenceListAttribute.get(0);
        final ExampleDataB second = update.referenceListAttribute.get(1);
        update.referenceListAttribute.clear();
        update.referenceListAttribute.add(second);
        update.referenceListAttribute.add(first);

        final MergeDiffInfo merge = merge(current, orginal, update);
        Assert.assertTrue(merge(current, orginal, update).hasNoConflicts());
        Assert.assertEquals(2, current.referenceListAttribute.size());
        Assert.assertEquals(newValue2.getId(), current.referenceListAttribute.get(0).getId());
        Assert.assertEquals(newValue1.getId(), current.referenceListAttribute.get(1).getId());
    }
}