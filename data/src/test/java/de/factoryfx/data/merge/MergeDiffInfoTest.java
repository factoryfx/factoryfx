package de.factoryfx.data.merge;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import org.junit.Test;

public class MergeDiffInfoTest {

    @Test
    public void test_json(){
        final ArrayList<MergeResultEntry> mergeInfos = new ArrayList<>();
        mergeInfos.add(new MergeResultEntry(new ExampleFactoryA(),new StringAttribute(new AttributeMetadata()), Optional.empty()));
        MergeDiff mergeDiff=new MergeDiff(mergeInfos,new ArrayList<>());

        ObjectMapperBuilder.build().copy(new MergeDiffInfo(mergeDiff));
    }

}