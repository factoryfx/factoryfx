package io.github.factoryfx.data.merge;

import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import io.github.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReferenceMergeTest extends MergeHelperTestBase{


    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().addBackReferences();

        ExampleDataA newVersion = current.internal().copy();

        MergeDiffInfo merge = this.merge(current, current, newVersion);
        Assertions.assertTrue( merge.hasNoConflicts());
        Assertions.assertEquals(0,merge.mergeInfos.size());
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
        Assertions.assertTrue(merge(current, current, update).hasNoConflicts());
        Assertions.assertEquals(beforeMergeId, current.referenceAttribute.get().getId());
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

        Assertions.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assertions.assertEquals(null, current.referenceAttribute.get());
    }


}