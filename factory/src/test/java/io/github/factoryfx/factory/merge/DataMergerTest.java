package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DataMergerTest {

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
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        Assertions.assertEquals("1111111",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getPreviousRootData()));
        Assertions.assertEquals("2222222",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
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
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
        ExampleDataB exampleDataB = (ExampleDataB) mergeDiff.mergeInfos.get(0).getAttribute(mergeDiff.getPreviousRootData()).get();
        Assertions.assertTrue((exampleDataB.stringAttribute.get()).equals("Factory to be deleted"));
        Assertions.assertEquals("empty",mergeDiff.mergeInfos.get(0).getAttributeDisplayText(mergeDiff.getNewRootData()));
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
        Assertions.assertTrue(mergeDiff.hasNoConflicts());
    }

    public static class ExampleFactoryPermission extends FactoryBase<Void, ExampleFactoryPermission> {
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

        Assertions.assertFalse(mergeDiff.hasNoPermissionViolation());
        Assertions.assertFalse(mergeDiff.successfullyMerged());
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
        Assertions.assertTrue(mergeDiff.hasNoPermissionViolation());
        Assertions.assertTrue(mergeDiff.successfullyMerged());
    }

    @Test
    public void test_performance_merge(){
        ExampleDataA currentModel = new ExampleDataA();
        for (int i=0;i<100000;i++){
            ExampleDataB dataB = new ExampleDataB();
            dataB.referenceAttributeC.set(new ExampleDataC());
            currentModel.referenceListAttribute.add(dataB);
        }
        currentModel.stringAttribute.set("1111111");

        currentModel=currentModel.internal().addBackReferences();
        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        int jreDeoptmizer=0;
        for (int i = 0; i < 1; i++) {
            newModel.stringAttribute.set(""+i);
            DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);
            MergeDiffInfo<ExampleDataA> mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);
            jreDeoptmizer++;
            originalModel.stringAttribute.set(""+i);
        }
        System.out.println(jreDeoptmizer);

//        Assertions.assertTrue(mergeDiff.hasNoConflicts());

//        Assertions.assertTrue(mergeDiff.hasNoConflicts());
//        Assertions.assertEquals("2222222",currentModel.stringAttribute.get());
    }

}