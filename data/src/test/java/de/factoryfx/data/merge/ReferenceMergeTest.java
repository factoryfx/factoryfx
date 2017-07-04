package de.factoryfx.data.merge;

import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceMergeTest extends MergeHelperTestBase{


    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);

        ExampleDataA newVersion = new ExampleDataA();
        newVersion.referenceAttribute.set(newValue);

        Assert.assertTrue( this.merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(newValue, current.referenceAttribute.get());
    }

    @Test
    public void test_merge_change(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);

        ExampleDataA newVersion = new ExampleDataA();
        ExampleDataB newValue2 = new ExampleDataB();
        newVersion.referenceAttribute.set(newValue2);

        Assert.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(newValue2, current.referenceAttribute.get());
    }

    @Test
    public void test_delte(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);

        ExampleDataA newVersion = new ExampleDataA();
        newVersion.referenceAttribute.set(null);

        Assert.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(null, current.referenceAttribute.get());
    }


}