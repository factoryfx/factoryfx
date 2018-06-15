package de.factoryfx.data.merge;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceMergeTest extends MergeHelperTestBase{


    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().addBackReferences();

        ExampleDataA newVersion = current.internal().copy();

        MergeDiffInfo merge = this.merge(current, current, newVersion);
        Assert.assertTrue( merge.hasNoConflicts());
        Assert.assertEquals(0,merge.mergeInfos.size());
    }

    @Test
    public void test_merge_change(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().addBackReferences();

        ExampleDataA update = new ExampleDataA();
        ExampleDataB newValue2 = new ExampleDataB();
        update.referenceAttribute.set(newValue2);
        update = update.internal().addBackReferences();

        String beforeMergeId=update.referenceAttribute.get().getId();
        Assert.assertTrue(merge(current, current, update).hasNoConflicts());
        Assert.assertEquals(beforeMergeId, current.referenceAttribute.get().getId());
    }

    @Test
    public void test_delte(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().addBackReferences();

        ExampleDataA newVersion = new ExampleDataA();
        newVersion.referenceAttribute.set(null);
        newVersion = newVersion.internal().addBackReferences();

        Assert.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(null, current.referenceAttribute.get());
    }


}