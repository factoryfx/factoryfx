package de.factoryfx.factory.merge;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class FactoryMergerMergerTest {

    @Test
    public void test_diff_info(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.copy();
        ExampleFactoryA newModel = currentModel.copy();
        newModel.stringAttribute.set("2222222");
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("ExampleA1: 2222222",mergeDiff.getMergeInfos().get(0).mergeResultEntryInfo.newValueValueDisplayText);
        Assert.assertEquals("ExampleA1: 1111111",mergeDiff.getMergeInfos().get(0).mergeResultEntryInfo.previousValueDisplayText);
    }

    @Test
    public void test_diff_info_null(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryA originalModel = currentModel.copy();
        ExampleFactoryA newModel = currentModel.copy();
        newModel.referenceAttribute.set(null);
        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("ExampleA2:empty",mergeDiff.getMergeInfos().get(0).mergeResultEntryInfo.newValueValueDisplayText);
        Assert.assertTrue(mergeDiff.getMergeInfos().get(0).mergeResultEntryInfo.previousValueDisplayText.contains(exampleFactoryB.getId()));
    }

    @Test
    public void test_diff_info_mergediff_for_root(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryA originalModel = currentModel.copy();
        ExampleFactoryA newModel = currentModel.copy();

        newModel.referenceAttribute.set(null);

        FactoryMerger factoryMerger = new FactoryMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals(currentModel,mergeDiff.getMergeInfos().get(0).parent);
    }

}