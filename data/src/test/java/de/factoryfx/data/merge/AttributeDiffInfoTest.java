package de.factoryfx.data.merge;

import java.util.ArrayList;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Test;

public class AttributeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<AttributeDiffInfo> mergeInfos = new ArrayList<>();
        mergeInfos.add(new AttributeDiffInfo("jgj","id"));
        MergeDiffInfo mergeDiff=new MergeDiffInfo(mergeInfos,new ArrayList<>(),new ArrayList<>(),new ExampleDataA(),new ExampleDataA());

        ObjectMapperBuilder.build().copy(mergeDiff);
    }

}