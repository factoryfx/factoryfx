package de.factoryfx.data.merge;

import java.util.stream.Collectors;

import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceListMergeTest extends MergeHelperTestBase{

    @Test
    public void test_same(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue1 = new ExampleFactoryB();
        newValue1.stringAttribute.set("1111");
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        newValue2.stringAttribute.set("2222");
        ExampleFactoryB newValue3 = new ExampleFactoryB();
        newValue3.stringAttribute.set("2222");
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleFactoryA aTest2 = new ExampleFactoryA();
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
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue1 = new ExampleFactoryB();
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        ExampleFactoryB newValue3 = new ExampleFactoryB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleFactoryA aTest2 = new ExampleFactoryA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);
        ExampleFactoryB replacedValue = new ExampleFactoryB();
        aTest2.referenceListAttribute.add(replacedValue);

        Assert.assertTrue(merge(current, current, aTest2).hasNoConflicts());
        Assert.assertEquals(3, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1, current.referenceListAttribute.get(0));
        Assert.assertEquals(newValue2, current.referenceListAttribute.get(1));
        Assert.assertEquals(replacedValue, current.referenceListAttribute.get(2));
    }

    @Test
    public void test_1_current_added(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue1 = new ExampleFactoryB();
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);

        ExampleFactoryA orginal = current.internal().copy();
        ExampleFactoryB replacedValue = new ExampleFactoryB();
        current.referenceListAttribute.add(replacedValue);

        ExampleFactoryA aTest2 = new ExampleFactoryA();
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
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue1 = new ExampleFactoryB();
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        ExampleFactoryB newValue3 = new ExampleFactoryB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);


        ExampleFactoryA aTest2 = new ExampleFactoryA();
        aTest2.referenceListAttribute.add(newValue1);
        aTest2.referenceListAttribute.add(newValue2);

        Assert.assertTrue(merge(current, current, aTest2).hasNoConflicts());
        Assert.assertEquals(2, current.referenceListAttribute.size());
        Assert.assertEquals(newValue1, current.referenceListAttribute.get(0));
        Assert.assertEquals(newValue2, current.referenceListAttribute.get(1));
    }

    @Test
    public void test_both_added(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue1 = new ExampleFactoryB();
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);


        ExampleFactoryA orginal = current.internal().copy();
        ExampleFactoryA update = current.internal().copy();

        ExampleFactoryB newValue3 = new ExampleFactoryB();
        current.referenceListAttribute.add(newValue3);

        ExampleFactoryB newValue4 = new ExampleFactoryB();
        update.referenceListAttribute.add(newValue4);

        Assert.assertTrue(merge(current, orginal, update).hasNoConflicts());
        Assert.assertEquals(4, current.referenceListAttribute.size());
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue1.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue2.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue3.getId()));
        Assert.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue4.getId()));
    }


}