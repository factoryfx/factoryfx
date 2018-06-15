package de.factoryfx.data.merge;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        currentModel=currentModel.internal().addBackReferences();

        ExampleDataA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleDataA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);

        MergeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeDiff);
        Assert.assertEquals("1111111",((ExampleDataA)copy.getPreviousRootData()).stringAttribute.get());
        Assert.assertEquals("2222222",((ExampleDataA)copy.getNewRootData()).stringAttribute.get());
        Assert.assertNotNull(((ExampleDataA)copy.getNewRootData()).internal().getRoot());


    }

}