package de.factoryfx.data.merge;

import java.util.Arrays;
import java.util.stream.Collectors;

import de.factoryfx.data.DataTest;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import de.factoryfx.data.merge.testfactories.ExampleDataC;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MergeTest {


    @Test
    public void test_merge_no_change(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_no_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2222222",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_only_local_chnage(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("333333333");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("333333333",currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("333333333");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("333333333", currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_change(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB oldReference = new ExampleDataB();
        oldReference.stringAttribute.set("1");
        currentModel.referenceAttribute.set(oldReference);

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        ExampleDataB newReference = new ExampleDataB();
        newReference.stringAttribute.set("2");
        newModel.referenceAttribute.set(newReference);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2", currentModel.referenceAttribute.get().stringAttribute.get());

    }

    @Test
    public void test_merge_reference_delete(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.referenceAttribute.get());

    }

    @Test
    public void test_merge_reference_delete_2(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(0, mergeDiff.getConflictCount());
        Assert.assertEquals(null, currentModel.referenceAttribute.get());
    }


    @Test
    public void test_merge_reference_delete_with_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("1111111");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("qqqqqqqq", currentModel.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set(null);
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set(null);
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111qqqqqqqqq");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_new(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set(null);
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_list_no_change(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get().get(0).stringAttribute.set("3333333333");
            newModel.referenceListAttribute.get().get(1).stringAttribute.set("444444444");
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("3333333333", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("444444444", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {

        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("3333333");
            newModel.referenceListAttribute.get(1).stringAttribute.set("44444444");
        }


        currentModel.referenceListAttribute.get(0).stringAttribute.set("3333333333qqqqq");
        currentModel.referenceListAttribute.get(1).stringAttribute.set("444444444qqqq");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(2, mergeDiff.getConflictCount());
        Assert.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("3333333333qqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("444444444qqqq", currentModel.referenceListAttribute.get(1).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict_new_item(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("33333333333");
                newModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(3, currentModel.referenceListAttribute.size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assert.assertEquals("33333333333", currentModel.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Test
    public void test_merge_list_update_no_conflict_delete_item(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get().clear();
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(0, currentModel.referenceListAttribute.get().size());
    }

    @Test
    public void test_merge_list_add_item_current(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {

        }

        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("333333333333");
        currentModel.referenceListAttribute.get().add(exampleFactoryB);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(3, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals("1111111111", currentModel.referenceListAttribute.get().get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get().get(1).stringAttribute.get());
        Assert.assertEquals("333333333333", currentModel.referenceListAttribute.get().get(2).stringAttribute.get());
    }

    @Test
    public void test_merge_list_add_item_current_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1111111111");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2222222222");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("33333333");
        }

        currentModel.referenceListAttribute.get(0).stringAttribute.set("11111qqqqqqqqq"); //conflict
        {
            ExampleDataB exampleFactoryB = new ExampleDataB();
            exampleFactoryB.stringAttribute.set("4444444444444");
            currentModel.referenceListAttribute.get().add(exampleFactoryB);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals(3, currentModel.referenceListAttribute.size());
        Assert.assertEquals("11111qqqqqqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assert.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assert.assertEquals("4444444444444", currentModel.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Ignore
    @Test
    public void test_merge_list_both_added(){
        ExampleDataA currentModel = new ExampleDataA();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("1");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("2");
                currentModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            {
                ExampleDataB exampleFactoryB = new ExampleDataB();
                exampleFactoryB.stringAttribute.set("3");
                newModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }
        {
            ExampleDataB exampleFactoryB = new ExampleDataB();
            exampleFactoryB.stringAttribute.set("4");
            currentModel.referenceListAttribute.get().add(exampleFactoryB);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(0, mergeDiff.getConflictCount());
        Assert.assertEquals(4, currentModel.referenceListAttribute.get().size());
        Assert.assertEquals(Arrays.asList("1","2","3","4"),currentModel.referenceListAttribute.get().stream().map(p->p.stringAttribute.get()).sorted().collect(Collectors.toList()));
    }


    @Test
    public void test_duplicate_ids_bug() {
        ExampleDataA currentModel = new ExampleDataA();
        {
            ExampleDataB exampleFactoryB = new ExampleDataB();
            currentModel.referenceListAttribute.add(exampleFactoryB);
            currentModel.referenceAttribute.set(exampleFactoryB);
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {

        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            currentModel.referenceAttribute.set(currentModel.referenceAttribute.get().internal().copy());

        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff = dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        Assert.assertEquals(currentModel.referenceListAttribute.get(0), currentModel.referenceAttribute.get());
        //assert still serializable;
        currentModel.internal().copy();
    }

    @Test
    public void test_duplicate_ids_bug__nested_added_same_id() {
        ExampleDataA currentModel = new ExampleDataA();
        {
            ExampleDataB exampleFactoryB = new ExampleDataB();
            currentModel.referenceListAttribute.add(exampleFactoryB);
            currentModel.referenceAttribute.set(exampleFactoryB);

            exampleFactoryB.referenceAttributeC.set(new ExampleDataC());
        }

        ExampleDataA originalModel = currentModel.internal().copy();
        {

        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            ExampleDataB value = new ExampleDataB();
            value.referenceAttributeC.set(currentModel.referenceAttribute.get().referenceAttributeC.get().internal().copy());
            currentModel.referenceAttribute.set(value);
        }

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff = dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        //assert still serializable;
        currentModel.internal().copy();
    }

    @Test
    public void test_merge_conflict_but_resolvable_cause_set_to_same_value(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("3");
        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("3");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("3");

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current_reference(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.referenceAttribute.set(newValueInCurrent);

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.set(new ExampleDataB());

        ExampleDataA newModel = originalModel.internal().copy();


        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceAttribute.get().getId());
    }

    @Test
    public void test_no_change_different_current_referencelist(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.referenceListAttribute.add(newValueInCurrent);

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceListAttribute.get().clear();
        originalModel.referenceListAttribute.add(new ExampleDataB());

        ExampleDataA newModel = originalModel.internal().copy();


        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceListAttribute.get(0).getId());
    }

    @Test
    public void test_reflist_noconflict(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.referenceListAttribute.add(newValueInCurrent);

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        originalModel.referenceListAttribute.get().clear();
        originalModel.referenceListAttribute.add(new ExampleDataB());

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(newValueInCurrent.getId(),currentModel.referenceListAttribute.get(0).getId());
    }


    @Test
    public void test_copy_withObjectValue(){
        DataTest.ExampleObjectProperty currentModel = new DataTest.ExampleObjectProperty();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.objectValueAttribute.set("test2");

        DataTest.ExampleObjectProperty originalModel = currentModel.internal().copy();

        DataTest.ExampleObjectProperty newModel = currentModel.internal().copy();
        newModel.objectValueAttribute.set(null);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("test2",currentModel.objectValueAttribute.get());

    }
    

}
