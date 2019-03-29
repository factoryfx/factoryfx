package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals("1111111",((ExampleDataA)copy.getPreviousRootData()).stringAttribute.get());
        Assertions.assertEquals("2222222",((ExampleDataA)copy.getNewRootData()).stringAttribute.get());
        Assertions.assertNotNull((copy.getNewRootData()).internal().getRoot());


    }

}