package de.factoryfx.data.merge;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class MergeResultEntryTest {

    @Test
    public void json_serialisable(){
        AttributeDiffInfo mergeResultEntry = new AttributeDiffInfo("fdd","dgdgdg");
        AttributeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
//        Assert.assertEquals("sfdsf",copy.createPreviousAttribute().internal_getPreferredLabelText(Locale.ENGLISH));
    }
}