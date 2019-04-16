package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class MergeResultEntryTest {

    @Test
    public void json_serialisable(){
        AttributeDiffInfo mergeResultEntry = new AttributeDiffInfo("fdd", UUID.randomUUID());
        AttributeDiffInfo copy = ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
//        Assertions.assertEquals("sfdsf",copy.createPreviousAttribute().internal_getPreferredLabelText(Locale.ENGLISH));
    }
}