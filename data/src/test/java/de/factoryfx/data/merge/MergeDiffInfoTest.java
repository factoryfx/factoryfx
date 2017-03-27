package de.factoryfx.data.merge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<MergeResultEntry> mergeInfos = new ArrayList<>();
        mergeInfos.add(new MergeResultEntry("jgj",new StringAttribute(new AttributeMetadata()), Optional.empty()));
        MergeDiff mergeDiff=new MergeDiff(mergeInfos,new ArrayList<>(),new HashSet<>());

        ObjectMapperBuilder.build().copy(new MergeDiffInfo(mergeDiff));
    }

}