package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
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

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("2222222",mergeDiff.mergeInfos.get(0).getNewAttributeDisplayText());
        Assert.assertEquals("1111111",mergeDiff.mergeInfos.get(0).getPreviousAttributeDisplayText());
    }

    @Test
    public void test_diff_info_null(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("Factory to be deleted");
        currentModel.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryA originalModel = currentModel.internal().copy();
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
        Assert.assertEquals("empty",mergeDiff.mergeInfos.get(0).getNewAttributeDisplayText());
        Assert.assertTrue((((ExampleFactoryB)mergeDiff.mergeInfos.get(0).createPreviousAttribute().get()).stringAttribute.get()).equals("Factory to be deleted"));
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

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoConflicts());
    }

    public static class ExampleFactoryPermission extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").permission("permissionX");
    }

    @Test
    public void test_permission_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->false);

        Assert.assertFalse(mergeDiff.hasNoPermissionViolation());
        Assert.assertFalse(mergeDiff.successfullyMerged());
    }

    @Test
    public void test_permission_no_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assert.assertTrue(mergeDiff.hasNoPermissionViolation());
        Assert.assertTrue(mergeDiff.successfullyMerged());
    }

}