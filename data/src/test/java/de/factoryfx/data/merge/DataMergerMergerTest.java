package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;
import org.junit.Assert;
import org.junit.Test;

public class DataMergerMergerTest {

    @Test
    public void test_diff_info(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");

        currentModel=currentModel.internal().addBackReferences();
        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger<ExampleDataA>dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("1111111",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getPreviousRootData()));
        Assert.assertEquals("2222222",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
    }

    @Test
    public void test_diff_info_null(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("Factory to be deleted");
        currentModel.referenceAttribute.set(exampleFactoryB);

        currentModel=currentModel.internal().addBackReferences();
        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        ExampleDataB exampleDataB = (ExampleDataB) mergeDiff.mergeInfos.get(0).getAttribute(mergeDiff.getPreviousRootData()).get();
        Assert.assertTrue((exampleDataB.stringAttribute.get()).equals("Factory to be deleted"));
        Assert.assertEquals("empty",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
    }

    @Test
    public void test_diff_info_mergediff_for_root(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        currentModel=currentModel.internal().addBackReferences();
        currentModel=currentModel.internal().addBackReferences();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        newModel.referenceAttribute.set(null);

        DataMerger<ExampleDataA>dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
    }

    public static class ExampleFactoryPermission extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").permission("permissionX");
    }

    @Test
    public void test_permission_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        currentModel=currentModel.internal().addBackReferences();
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger<ExampleFactoryPermission> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo<ExampleFactoryPermission> mergeDiff= dataMerger.mergeIntoCurrent((permission)->false);

        Assert.assertFalse(mergeDiff.hasNoPermissionViolation());
        Assert.assertFalse(mergeDiff.successfullyMerged());
    }

    @Test
    public void test_permission_no_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        currentModel=currentModel.internal().addBackReferences();
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger<ExampleFactoryPermission> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo<ExampleFactoryPermission> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoPermissionViolation());
        Assert.assertTrue(mergeDiff.successfullyMerged());
    }

}