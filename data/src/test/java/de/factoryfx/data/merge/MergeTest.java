package de.factoryfx.data.merge;

import java.util.Arrays;
import java.util.stream.Collectors;

import de.factoryfx.data.DataTest;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import de.factoryfx.data.merge.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class MergeTest {


    @Test
    public void test_merge_no_change(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_no_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2222222",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_only_local_chnage(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("333333333");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("333333333",currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("333333333");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("333333333", currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_change(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB oldReference = new ExampleFactoryB();
        oldReference.stringAttribute.set("1");
        currentModel.referenceAttribute.set(oldReference);

        ExampleFactoryA originalModel = currentModel.internal().copy();

        ExampleFactoryA newModel = currentModel.internal().copy();
        ExampleFactoryB newReference = new ExampleFactoryB();
        newReference.stringAttribute.set("2");
        newModel.referenceAttribute.set(newReference);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2", currentModel.referenceAttribute.get().stringAttribute.get());

    }

    @Test
    public void test_merge_reference_delete(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.referenceAttribute.set(new ExampleFactoryB());
        ExampleFactoryA originalModel = currentModel.internal().copy();

        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.referenceAttribute.get());

    }

    @Test
    public void test_merge_reference_delete_2(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.referenceAttribute.set(new ExampleFactoryB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());
        Assert.assertEquals(null, currentModel.referenceAttribute.get());
    }


    @Test
    public void test_merge_reference_delete_with_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.referenceAttribute.set(new ExampleFactoryB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("1111111");

        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("qqqqqqqq", currentModel.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set(null);
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set(null);
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111qqqqqqqqq");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_new(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set(null);
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_list_no_change(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get().get(0).stringAttribute.set("3333333333");
            newModel.referenceListAttribute.get().get(1).stringAttribute.set("444444444");
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("3333333333", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("444444444", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {

        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("3333333");
            newModel.referenceListAttribute.get(1).stringAttribute.set("44444444");
        }


        currentModel.referenceListAttribute.get(0).stringAttribute.set("3333333333qqqqq");
        currentModel.referenceListAttribute.get(1).stringAttribute.set("444444444qqqq");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(2, mergeDiff.getConflictCount());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("3333333333qqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("444444444qqqq", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict_new_item(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("33333333333");
                newModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(3, currentModel.referenceListAttribute.size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assert.assertEquals("33333333333", currentModel.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict_delete_item(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get().clear();
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(0, currentModel.referenceListAttribute.get().size());
    }

    @Test
    public void test_merge_list_add_item_current(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {

        }

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("333333333333");
        currentModel.referenceListAttribute.get().add(exampleFactoryB);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(3, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get().get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get().get(1).stringAttribute.get());
        Assert.assertEquals("333333333333", currentModel.referenceListAttribute.get().get(2).stringAttribute.get());
    }

    @Test
    public void test_merge_list_add_item_current_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("33333333");
        }

        currentModel.referenceListAttribute.get(0).stringAttribute.set("11111qqqqqqqqq"); //conflict
        {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("4444444444444");
            currentModel.referenceListAttribute.get().add(exampleFactoryB);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals(3, currentModel.referenceListAttribute.size());
        Assert.assertEquals("11111qqqqqqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assert.assertEquals("4444444444444", currentModel.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Test
    public void test_merge_list_both_added(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("1");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("2");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {
        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("3");
                newModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }
        {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("4");
            currentModel.referenceListAttribute.get().add(exampleFactoryB);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());
        Assert.assertEquals(4, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals(Arrays.asList("1","2","3","4"),currentModel.referenceListAttribute.get().stream().map(p->p.stringAttribute.get()).sorted().collect(Collectors.toList()));
    }


    @Test
    public void test_duplicate_ids_bug() {
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            currentModel.referenceListAttribute.add(exampleFactoryB);
            currentModel.referenceAttribute.set(exampleFactoryB);
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {

        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            currentModel.referenceAttribute.set(currentModel.referenceAttribute.get().internal().copy());

        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff = dataMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        Assert.assertEquals(currentModel.referenceListAttribute.get(0), currentModel.referenceAttribute.get());
        //assert still serializable;
        currentModel.internal().copy();
    }

    @Test
    public void test_duplicate_ids_bug__nested_added_same_id() {
        ExampleFactoryA currentModel = new ExampleFactoryA();
        {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            currentModel.referenceListAttribute.add(exampleFactoryB);
            currentModel.referenceAttribute.set(exampleFactoryB);

            exampleFactoryB.referenceAttributeC.set(new ExampleFactoryC());
        }

        ExampleFactoryA originalModel = currentModel.internal().copy();
        {

        }

        ExampleFactoryA newModel = currentModel.internal().copy();
        {
            ExampleFactoryB value = new ExampleFactoryB();
            value.referenceAttributeC.set(currentModel.referenceAttribute.get().referenceAttributeC.get().internal().copy());
            currentModel.referenceAttribute.set(value);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff = dataMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        //assert still serializable;
        currentModel.internal().copy();
    }

    @Test
    public void test_merge_conflict_but_resolvable_cause_set_to_same_value(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("3");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("3");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("3");

        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");

        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current_reference(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB newValueInCurrent = new ExampleFactoryB();
        currentModel.referenceAttribute.set(newValueInCurrent);

        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.set(new ExampleFactoryB());

        ExampleFactoryA newModel = originalModel.internal().copy();


        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceAttribute.get().getId());
    }

    @Test
    public void test_no_change_different_current_referencelist(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB newValueInCurrent = new ExampleFactoryB();
        currentModel.referenceListAttribute.add(newValueInCurrent);

        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.referenceListAttribute.get().clear();
        originalModel.referenceListAttribute.add(new ExampleFactoryB());

        ExampleFactoryA newModel = originalModel.internal().copy();


        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceListAttribute.get(0).getId());
    }

    @Test
    public void test_reflist_noconflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB newValueInCurrent = new ExampleFactoryB();
        currentModel.referenceListAttribute.add(newValueInCurrent);

        ExampleFactoryA originalModel = currentModel.internal().copy();

        ExampleFactoryA newModel = currentModel.internal().copy();
        originalModel.referenceListAttribute.get().clear();
        originalModel.referenceListAttribute.add(new ExampleFactoryB());

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceListAttribute.get(0).getId());
    }


    @Test
    public void test_copy_withObjectValue(){
        DataTest.ExampleObjectProperty currentModel = new DataTest.ExampleObjectProperty();
        ExampleFactoryB newValueInCurrent = new ExampleFactoryB();
        currentModel.objectValueAttribute.set("test2");

        DataTest.ExampleObjectProperty originalModel = currentModel.internal().copy();

        DataTest.ExampleObjectProperty newModel = currentModel.internal().copy();
        newModel.objectValueAttribute.set(null);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("test2",currentModel.objectValueAttribute.get());

    }
    

}
