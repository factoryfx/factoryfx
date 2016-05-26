package de.factoryfx.factory.merge;

import java.util.Arrays;
import java.util.stream.Collectors;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

public class MergeTest {


    @Test
    public void test_merge_no_change(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("1111111");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_no_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("2222222");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2222222",currentModel.stringAttribute.get());

    }

    @Test
    public void test_merge_only_local_chnage(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("333333333");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("1111111");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("333333333",currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("333333333");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("2222222");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("333333333", currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_change(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB oldReference = new ExampleFactoryB();
        oldReference.stringAttribute.set("1");
        currentModel.referenceAttribute.set(oldReference);

        ExampleFactoryA originalModel = copy(currentModel);

        ExampleFactoryA newModel = copy(currentModel);
        ExampleFactoryB newReference = new ExampleFactoryB();
        newReference.stringAttribute.set("2");
        newModel.referenceAttribute.set(newReference);

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2", currentModel.referenceAttribute.get().stringAttribute.get());

    }

    @Test
    public void test_merge_reference_delete(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.referenceAttribute.set(new ExampleFactoryB());
        ExampleFactoryA originalModel = copy(currentModel);

        ExampleFactoryA newModel = copy(currentModel);
        newModel.referenceAttribute.set(null);
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.referenceAttribute.get());

    }

    @Test
    public void test_merge_reference_delete_with_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.referenceAttribute.set(new ExampleFactoryB());
        currentModel.referenceAttribute.get().stringAttribute.set("qqqqqqqq");

        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.referenceAttribute.get().stringAttribute.set("1111111");

        ExampleFactoryA newModel = copy(currentModel);
        newModel.referenceAttribute.set(null);
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals("qqqqqqqq", currentModel.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set(null);
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("1111111");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }

    @Test
    public void test_merge_reference_delete_in_current_conflict(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set(null);
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("1111111qqqqqqqqq");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertEquals(1, mergeDiff.getConflictCount());
        Assert.assertEquals(null, currentModel.stringAttribute.get());
    }


    @Test
    public void test_merge_new(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set(null);
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set(null);
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleFactoryA newModel = copy(currentModel);
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            newModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
//            IdentifiedParticipant identifiedParticipant1 = createIdentifiedParticipant();
//            identifiedParticipant1.pima.set("1111111111");
//            IdentifiedParticipant identifiedParticipant2 = createIdentifiedParticipant();
//            identifiedParticipant2.pima.set("2222222222");
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant1);
//            originalModel.messageRoutingModel.get().allParticipants.add(identifiedParticipant2);
        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            newModel.referenceListAttribute.get().get(0).stringAttribute.set("3333333333");
            newModel.referenceListAttribute.get().get(1).stringAttribute.set("444444444");
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {

        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("3333333");
            newModel.referenceListAttribute.get(1).stringAttribute.set("44444444");
        }


        currentModel.referenceListAttribute.get(0).stringAttribute.set("3333333333qqqqq");
        currentModel.referenceListAttribute.get(1).stringAttribute.set("444444444qqqq");

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("33333333333");
                newModel.referenceListAttribute.get().add(exampleFactoryB);
            }
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            newModel.referenceListAttribute.get().clear();
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
        }

        ExampleFactoryA newModel = copy(currentModel);
        {

        }

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("333333333333");
        currentModel.referenceListAttribute.get().add(exampleFactoryB);

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            newModel.referenceListAttribute.get(0).stringAttribute.set("33333333");
        }

        currentModel.referenceListAttribute.get(0).stringAttribute.set("11111qqqqqqqqq"); //conflict
        {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.stringAttribute.set("4444444444444");
            currentModel.referenceListAttribute.get().add(exampleFactoryB);
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {
        }

        ExampleFactoryA newModel = copy(currentModel);
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

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
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

        ExampleFactoryA originalModel = copy(currentModel);
        {

        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            currentModel.referenceAttribute.set(currentModel.referenceAttribute.get().copy());

        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff = factoryMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        Assert.assertEquals(currentModel.referenceListAttribute.get(0), currentModel.referenceAttribute.get());
        //assert still serializable;
        copy(currentModel);
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

        ExampleFactoryA originalModel = copy(currentModel);
        {

        }

        ExampleFactoryA newModel = copy(currentModel);
        {
            ExampleFactoryB value = new ExampleFactoryB();
            value.referenceAttributeC.set(currentModel.referenceAttribute.get().referenceAttributeC.get().copy());
            currentModel.referenceAttribute.set(value);
        }

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);
        MergeDiff mergeDiff = factoryMerger.mergeIntoCurrent();
        Assert.assertEquals(0, mergeDiff.getConflictCount());

        //assert still serializable;
        copy(currentModel);
    }

    @Test
    public void test_merge_conflict_but_resolvable_cause_set_to_same_value(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("3");
        ExampleFactoryA originalModel = copy(currentModel);
        originalModel.stringAttribute.set("1");
        ExampleFactoryA newModel = copy(currentModel);
        newModel.stringAttribute.set("3");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("3",currentModel.stringAttribute.get());

    }
    
    private <T extends FactoryBase<?,T>> T copy(T value){
        return  value.copy();
    }

}
