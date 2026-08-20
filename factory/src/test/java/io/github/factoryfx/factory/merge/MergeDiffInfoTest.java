package io.github.factoryfx.factory.merge;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.stringAttribute.set("1111111");
        currentModel=currentModel.internal().finalise();

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

    @Test
    public void test_json_validationErrors(){
        ExampleDataA currentModel = new ExampleDataA().internal().finalise();
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, currentModel.internal().copy(), currentModel.internal().copy());

        MergeDiffInfo<ExampleDataA> mergeDiff = new MergeDiffInfo<>(dataMerger.mergeIntoCurrent((permission)->true), List.of("error1","error2"));
        Assertions.assertTrue(mergeDiff.hasValidationErrors());
        Assertions.assertTrue(mergeDiff.successfullyMerged());

        MergeDiffInfo<ExampleDataA> copy = ObjectMapperBuilder.build().copy(mergeDiff);
        Assertions.assertEquals(List.of("error1","error2"), copy.validationErrors);
    }

    @Test
    public void test_json_withoutValidationErrorsField_yieldsEmptyList(){
        ExampleDataA currentModel = new ExampleDataA().internal().finalise();
        DataMerger<ExampleDataA> dataMerger = new DataMerger<>(currentModel, currentModel.internal().copy(), currentModel.internal().copy());
        MergeDiffInfo<ExampleDataA> mergeDiff = dataMerger.mergeIntoCurrent((permission)->true);

        ObjectNode json = (ObjectNode) ObjectMapperBuilder.build().valueToTree(mergeDiff);
        json.remove("validationErrors");//payload from a peer without the field

        MergeDiffInfo<?> copy = ObjectMapperBuilder.build().treeToValue(json, MergeDiffInfo.class);
        Assertions.assertTrue(copy.validationErrors.isEmpty());
    }

}