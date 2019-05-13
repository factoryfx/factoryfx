package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryBaseTest;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataMergerTest {

    @Test
    public void test_diff_info(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");

        currentModel=currentModel.internal().finalise();
        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger<ExampleDataA>dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        assertEquals("1111111",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getPreviousRootData()));
        assertEquals("2222222",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
    }

    @Test
    public void test_diff_info_null(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("Factory to be deleted");
        currentModel.referenceAttribute.set(exampleFactoryB);

        currentModel=currentModel.internal().finalise();
        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.referenceAttribute.set(null);
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        ExampleDataB exampleDataB = (ExampleDataB) mergeDiff.mergeInfos.get(0).getAttribute(mergeDiff.getPreviousRootData()).get();
        Assertions.assertTrue((exampleDataB.stringAttribute.get()).equals("Factory to be deleted"));
        assertEquals("empty",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
    }

    @Test
    public void test_diff_info_mergediff_for_root(){
        ExampleDataA currentModel = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        currentModel.referenceAttribute.set(exampleFactoryB);
        currentModel=currentModel.internal().finalise();
        currentModel=currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        newModel.referenceAttribute.set(null);

        DataMerger<ExampleDataA>dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
    }

    public static class ExampleFactoryPermission extends FactoryBase<Void, ExampleFactoryPermission> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").permission("permissionX");
    }

    @Test
    public void test_permission_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        currentModel=currentModel.internal().finalise();
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger<ExampleFactoryPermission> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo<ExampleFactoryPermission> mergeDiff= dataMerger.mergeIntoCurrent((permission)->false);

        Assertions.assertFalse(mergeDiff.hasNoPermissionViolation());
        Assertions.assertFalse(mergeDiff.successfullyMerged());
    }

    @Test
    public void test_permission_no_violation(){
        ExampleFactoryPermission currentModel = new ExampleFactoryPermission();
        currentModel.stringAttribute.set("123");
        currentModel=currentModel.internal().finalise();
        ExampleFactoryPermission originalModel = currentModel.internal().copy();
        ExampleFactoryPermission newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("XXX");

        DataMerger<ExampleFactoryPermission> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
        MergeDiffInfo<ExampleFactoryPermission> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
        Assertions.assertTrue(mergeDiff.hasNoPermissionViolation());
        Assertions.assertTrue(mergeDiff.successfullyMerged());
    }

    @Test
    public void test_getChangedFactories_no_change() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set("123");

        

        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(exampleFactoryA, exampleFactoryA.internal().copy(), copy);

        MergeResult<ExampleFactoryA> mergeResult = dataMerger.createMergeResult((p) -> true);
        assertEquals(0,mergeResult.getMergedFactories().size());

    }

    @Test
    public void test_getChangedFactories_views() {
        FactoryBaseTest.XRoot previous = new FactoryBaseTest.XRoot();
        FactoryBaseTest.ExampleFactoryAndViewA exampleFactoryAndViewA = new FactoryBaseTest.ExampleFactoryAndViewA();
        previous.referenceAttribute.set(exampleFactoryAndViewA);
        FactoryBaseTest.XFactory xFactory = new FactoryBaseTest.XFactory();
        previous.xFactory.set(xFactory);


        FactoryBaseTest.XRoot update= previous.utility().copy();
        update.xFactory.set(null);

        DataMerger<FactoryBaseTest.XRoot> dataMerger = new DataMerger<>(previous, previous.internal().copy(), update);
        MergeResult<FactoryBaseTest.XRoot> mergeResult = dataMerger.createMergeResult((p) -> true);
        Set<FactoryBase<?,FactoryBaseTest.XRoot>> changedFactories=mergeResult.getMergedFactories();
        Assertions.assertTrue(changedFactories.contains(previous));
        Assertions.assertTrue(changedFactories.contains(exampleFactoryAndViewA));
    }

    @Test
    public void test_getChangedFactories_simple() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set("qqqqq");


        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(exampleFactoryA, exampleFactoryA.internal().copy(), copy);
        MergeResult<ExampleFactoryA> mergeResult = dataMerger.createMergeResult((p) -> true);
        Set<FactoryBase<?,ExampleFactoryA>> changedFactories=mergeResult.getMergedFactories();
        Assertions.assertEquals(1,changedFactories.size());
        Assertions.assertTrue(changedFactories.contains(exampleFactoryA));
    }

    @Test
    public void test_getChangedFactories_simple_change_to_null() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("123");

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.stringAttribute.set(null);


        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(exampleFactoryA, exampleFactoryA.internal().copy(), copy);
        MergeResult<ExampleFactoryA> mergeResult = dataMerger.createMergeResult((p) -> true);
        Set<FactoryBase<?,ExampleFactoryA>> changedFactories=mergeResult.getMergedFactories();
        Assertions.assertEquals(1,changedFactories.size());
        Assertions.assertTrue(changedFactories.contains(exampleFactoryA));
    }

}