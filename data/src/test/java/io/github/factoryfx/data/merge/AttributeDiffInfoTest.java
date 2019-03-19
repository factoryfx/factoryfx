package io.github.factoryfx.data.merge;

import java.util.ArrayList;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

public class AttributeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<AttributeDiffInfo> mergeInfos = new ArrayList<>();
        mergeInfos.add(new AttributeDiffInfo("jgj","id"));
        MergeDiffInfo<ExampleDataA> mergeDiff=new MergeDiffInfo<>(mergeInfos,new ArrayList<>(),new ArrayList<>()
                ,ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA())
                ,ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),ExampleDataA.class);

        ObjectMapperBuilder.build().copy(mergeDiff);
    }

}