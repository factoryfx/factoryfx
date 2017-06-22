package de.factoryfx.data.merge;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        ExampleFactoryA currentModel = new ExampleFactoryA();
        currentModel.stringAttribute.set("1111111");
        ExampleFactoryA originalModel = currentModel.internal().copy();
        originalModel.stringAttribute.set("1111111");
        ExampleFactoryA newModel = currentModel.internal().copy();
        newModel.stringAttribute.set("2222222");
        DataMerger dataMerger = new DataMerger(currentModel, originalModel, newModel);

        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent((permission)->true);

        MergeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeDiff);
        Assert.assertEquals("1111111",((ExampleFactoryA)copy.getPreviousRootData()).stringAttribute.get());
        Assert.assertEquals("2222222",((ExampleFactoryA)copy.getNewRootData()).stringAttribute.get());

    }

}