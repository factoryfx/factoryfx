package de.factoryfx.data.merge;

import java.util.ArrayList;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<AttributeDiffInfo> mergeInfos = new ArrayList<>();
        mergeInfos.add(new AttributeDiffInfo("jgj",new StringAttribute(new AttributeMetadata())));
        MergeDiffInfo mergeDiff=new MergeDiffInfo(mergeInfos,new ArrayList<>(),new ArrayList<>());

        ObjectMapperBuilder.build().copy(mergeDiff);
    }

}