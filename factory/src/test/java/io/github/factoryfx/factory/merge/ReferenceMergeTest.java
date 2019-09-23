package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ReferenceMergeTest extends MergeHelperTestBase{


    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().finalise();

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
        current = current.internal().finalise();

        ExampleDataA update = new ExampleDataA();
        ExampleDataB newValue2 = new ExampleDataB();
        update.referenceAttribute.set(newValue2);
        update = update.internal().finalise();

        UUID beforeMergeId=update.referenceAttribute.get().getId();
        Assertions.assertTrue(merge(current, current, update).hasNoConflicts());
        Assertions.assertEquals(beforeMergeId, current.referenceAttribute.get().getId());
    }

    @Test
    public void test_delta(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().finalise();

        ExampleDataA newVersion = new ExampleDataA();
        newVersion.referenceAttribute.set(null);
        newVersion = newVersion.internal().finalise();

        Assertions.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assertions.assertEquals(null, current.referenceAttribute.get());
    }


    @Test
    public void test_keep_old_for_moved(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue = new ExampleDataB();
        current.referenceAttribute.set(newValue);
        current = current.internal().finalise();

        ExampleDataA newVersion = current.internal().copy();
        newVersion.referenceListAttribute.add(newVersion.referenceAttribute.get());
        newVersion.referenceAttribute.set(null);

        ExampleDataB oldRef = current.referenceAttribute.get();
        MergeDiffInfo merge = this.merge(current, current, newVersion);
        Assertions.assertEquals( oldRef,current.referenceListAttribute.get(0));

    }


}