package de.factoryfx.data.merge;

import java.util.ArrayList;

import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class AttributeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<AttributeDiffInfo> mergeInfos = new ArrayList<>();
        mergeInfos.add(new AttributeDiffInfo(
                new AttributeJsonWrapper(new StringAttribute(),""),
                new AttributeJsonWrapper(new StringAttribute(),""),
                        "jgj","id"));
        MergeDiffInfo mergeDiff=new MergeDiffInfo(mergeInfos,new ArrayList<>(),new ArrayList<>());

        ObjectMapperBuilder.build().copy(mergeDiff);
    }

}