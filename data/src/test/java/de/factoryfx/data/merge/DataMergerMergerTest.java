package de.factoryfx.data.merge;

import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class DataMergerMergerTest {

    @Test
    public void test_diff_info(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2222222",mergeDiff.getMergeInfos().get(0).createInfo(false).newValueValueDisplayText);
        Assert.assertEquals("1111111",mergeDiff.getMergeInfos().get(0).createInfo(false).previousValueDisplayText);
    }

    @Test
    public void test_diff_info_null(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryA originalModel = currentModel.internal().copy();
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("empty",mergeDiff.getMergeInfos().get(0).createInfo(false).newValueValueDisplayText);
        Assert.assertTrue(mergeDiff.getMergeInfos().get(0).createInfo(false).previousValueDisplayText.contains(exampleFactoryB.getId().toString()));
    }

    @Test
    public void test_diff_info_mergediff_for_root(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryA originalModel = currentModel.internal().copy();
        ExampleFactoryA newModel = currentModel.internal().copy();

        newModel.referenceAttribute.set(null);

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        Assert.assertTrue(mergeDiff.hasNoConflicts());
    }

}