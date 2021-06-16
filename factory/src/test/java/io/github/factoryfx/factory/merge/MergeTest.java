package io.github.factoryfx.factory.merge;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;


import io.github.factoryfx.factory.DataTest;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class MergeTest {


    @Test
    public void test_merge_no_change(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_no_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("2222222",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_only_local_chnage(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("333333333");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("333333333",currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("333333333");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(1, mergeDiff.getConflictCount());
        Assertions.assertEquals("333333333", currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_change(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB oldReference = new ExampleDataB();
        oldReference.stringAttribute.set("1");
        currentModel.referenceAttribute.set(oldReference);

        currentModel=currentModel.internal().finalise();
        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        ExampleDataB newReference = new ExampleDataB();
        newReference.stringAttribute.set("2");
        newModel.referenceAttribute.set(newReference);

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("2", currentModel.referenceAttribute.get().stringAttribute.get());

    }

    @Test
    public void test_merge_reference_delete(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(null, currentModel.referenceAttribute.get());

    }

    @Test
    public void test_merge_reference_delete_2(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(0, mergeDiff.getConflictCount());
        Assertions.assertEquals(null, currentModel.referenceAttribute.get());
    }


    @Test
    public void test_merge_reference_delete_with_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.referenceAttribute.get().stringAttribute.set("1111111");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(1, mergeDiff.getConflictCount());
        Assertions.assertEquals("qqqqqqqq", currentModel.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set(null);
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(null, currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current_conflict(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set(null);
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1111111qqqqqqqqq");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(1, mergeDiff.getConflictCount());
        Assertions.assertEquals(null, currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_new(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set(null);
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("1111111",currentModel.stringAttribute.get());

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
        currentModel=currentModel.internal().finalise();

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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assertions.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
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
        currentModel=currentModel.internal().finalise();

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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assertions.assertEquals("3333333333", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals("444444444", currentModel.referenceListAttribute.get(1).stringAttribute.get());
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
        currentModel=currentModel.internal().finalise();

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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(2, mergeDiff.getConflictCount());
        Assertions.assertEquals(2, currentModel.referenceListAttribute.get().size());
        Assertions.assertEquals("3333333333qqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals("444444444qqqq", currentModel.referenceListAttribute.get(1).stringAttribute.get());
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
        currentModel=currentModel.internal().finalise();

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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(3, currentModel.referenceListAttribute.size());
        Assertions.assertEquals("1111111111", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assertions.assertEquals("33333333333", currentModel.referenceListAttribute.get(2).stringAttribute.get());
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
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            newModel.referenceListAttribute.get().clear();
        }

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(0, currentModel.referenceListAttribute.get().size());
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
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        {
        }

        ExampleDataA newModel = currentModel.internal().copy();
        {

        }

        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("333333333333");
        currentModel.referenceListAttribute.get().add(exampleFactoryB);

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(3, currentModel.referenceListAttribute.get().size());
        Assertions.assertEquals("1111111111", currentModel.referenceListAttribute.get().get(0).stringAttribute.get());
        Assertions.assertEquals("2222222222", currentModel.referenceListAttribute.get().get(1).stringAttribute.get());
        Assertions.assertEquals("333333333333", currentModel.referenceListAttribute.get().get(2).stringAttribute.get());
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
        currentModel=currentModel.internal().finalise();

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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(1, mergeDiff.getConflictCount());
        Assertions.assertEquals(3, currentModel.referenceListAttribute.size());
        Assertions.assertEquals("11111qqqqqqqqq", currentModel.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals("2222222222", currentModel.referenceListAttribute.get(1).stringAttribute.get());
        Assertions.assertEquals("4444444444444", currentModel.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Disabled
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

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(0, mergeDiff.getConflictCount());
        Assertions.assertEquals(4, currentModel.referenceListAttribute.get().size());
        Assertions.assertEquals(Arrays.asList("1","2","3","4"),currentModel.referenceListAttribute.get().stream().map(p->p.stringAttribute.get()).sorted().collect(Collectors.toList()));
    }


    @Test
    public void test_duplicate_ids_bug() {
        ExampleDataA currentModel = new ExampleDataA();
        {
            ExampleDataB exampleFactoryB = new ExampleDataB();
            currentModel.referenceAttribute.set(exampleFactoryB);
        }
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        {

        }

        ExampleDataA newModel = currentModel.internal().copy();
        {

            newModel.referenceListAttribute.add(newModel.referenceAttribute.get());
//            newModel.referenceListAttribute.add(value);
//            newModel.referenceListAttribute.add(newModel.referenceAttribute.get().utility().semanticCopy());
        }

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff = dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(0, mergeDiff.getConflictCount());

        Assertions.assertEquals(currentModel.referenceAttribute.get(), currentModel.referenceListAttribute.get().get(0));
        //assert still serializable;
        ObjectMapperBuilder.build().copy(currentModel);
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
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        {

        }

        ExampleDataA newModel = currentModel.internal().copy();
        {
            ExampleDataB value = new ExampleDataB();
            value.referenceAttributeC.set(currentModel.referenceAttribute.get().referenceAttributeC.get().internal().copy());
            currentModel.referenceAttribute.set(value);
        }

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff = dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertEquals(0, mergeDiff.getConflictCount());

        //assert still serializable;
        ObjectMapperBuilder.build().copy(currentModel);
    }

    @Test
    public void test_merge_conflict_but_resolvable_cause_set_to_same_value(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("3");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("3");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("3");
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1");

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("1");

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("3",currentModel.stringAttribute.get());

    }

    @Test
    public void test_no_change_different_current_reference(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.referenceAttribute.set(new ExampleDataB());
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = originalModel.internal().copy();


        {
            ExampleDataB newValueInCurrent = new ExampleDataB();
            currentModel.referenceAttribute.set(newValueInCurrent);
        }


        UUID idBeforeMerge = currentModel.referenceAttribute.get().getId();
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(idBeforeMerge,currentModel.referenceAttribute.get().getId());
    }

    @Test
    public void test_no_change_different_current_referencelist(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.referenceListAttribute.add(newValueInCurrent);
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = originalModel.internal().copy();


        {
            currentModel.referenceListAttribute.get().clear();
            currentModel.referenceListAttribute.add(new ExampleDataB());
        }

        UUID idBeforeMerge = currentModel.referenceListAttribute.get(0).getId();

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);

        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(idBeforeMerge,currentModel.referenceListAttribute.get(0).getId());
    }

    @Test
    public void test_reflist_noconflict(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB newValueInCurrent = new ExampleDataB();
        currentModel.referenceListAttribute.add(newValueInCurrent);
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        {
            originalModel.referenceListAttribute.clear();
            originalModel.referenceListAttribute.add(new ExampleDataB());
        }

        UUID expectedId=currentModel.referenceListAttribute.get(0).getId();
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);

        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(expectedId,currentModel.referenceListAttribute.get(0).getId());
    }


    @Test
    public void test_copy_withObjectValue(){
        DataTest.ExampleObjectProperty currentModel = new DataTest.ExampleObjectProperty();
        currentModel.objectValueAttribute.set("test2");
        currentModel=currentModel.internal().finalise();

        DataTest.ExampleObjectProperty originalModel = currentModel.internal().copy();

        DataTest.ExampleObjectProperty newModel = currentModel.internal().copy();
        newModel.objectValueAttribute.set(null);

        DataMerger<DataTest.ExampleObjectProperty> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("test2",currentModel.objectValueAttribute.get());

    }


    @Test
    public void test_merge_root_after_add(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();

        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(new ExampleDataB());

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(currentModel.internal().getRoot(),currentModel.referenceAttribute.get().internal().getRoot());
    }

    @Test
    public void test_merge_parent_after_add(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        newModel.referenceAttribute.set(new ExampleDataB());
        newModel.internal().finalise();
        Assertions.assertTrue(newModel.referenceAttribute.get().internal().getParents().size()>0);

        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals(1,newModel.referenceAttribute.get().internal().getParents().size());
        Assertions.assertEquals(currentModel,currentModel.referenceAttribute.get().internal().getParents().iterator().next());
    }

    public static class ExampleDataView extends FactoryBase<Void,ExampleDataView> {
        public final FactoryViewAttribute<ExampleDataView,Void,ExampleDataBView> viewAttribute = new FactoryViewAttribute<>((r) -> {
            ExampleDataAView exampleDataAView = r.referenceAttribute.get();
            if (exampleDataAView!=null){
                return exampleDataAView.bAttribute.get();
            }
            return null;
        });
        public final FactoryAttribute<Void,ExampleDataAView> referenceAttribute = new FactoryAttribute<>();

    }

    public static class ExampleDataAView extends FactoryBase<Void, ExampleDataView> {
        public final FactoryAttribute<Void,ExampleDataBView> bAttribute = new FactoryAttribute<>();
    }

    public static class ExampleDataBView extends FactoryBase<Void, ExampleDataView> {

    }

    @Test
    public void test_merge_parent_after_add_view(){
        ExampleDataView currentModel = new ExampleDataView();
        currentModel.internal().finalise();

        ExampleDataView originalModel = currentModel.internal().copy();
        ExampleDataView newModel = currentModel.internal().copy();

        ExampleDataAView exampleDataAView = new ExampleDataAView();
        exampleDataAView.bAttribute.set(new ExampleDataBView());
        newModel.referenceAttribute.set(exampleDataAView);


        DataMerger<ExampleDataView> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());

//        currentModel.internal().finalise();

        Assertions.assertEquals(1,currentModel.referenceAttribute.get().bAttribute.get().internal().getParents().size());
        Assertions.assertTrue(currentModel.referenceAttribute.get().internal().getParents().contains(currentModel));
    }

    @Test
    public void test_parent_after_merge(){
        ExampleFactoryA original = new ExampleFactoryA();
        original.internal().finalise();

        ExampleFactoryA update= original.utility().copy();
        update.referenceAttribute.set(new ExampleFactoryB());

        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(original,original.utility().copy(),update);
        dataMerger.mergeIntoCurrent((p)->true);

        Assertions.assertEquals(1,original.referenceAttribute.get().internal().getParents().size());
        Assertions.assertEquals(original,original.referenceAttribute.get().internal().getParents().iterator().next());
    }



}
