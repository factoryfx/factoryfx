package de.factoryfx.data.merge;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class MergeResultEntryTest {


    @Test
    public void json_serialisable(){
        MergeResultEntryInfo  mergeResultEntry = new MergeResultEntryInfo("","", "", "");
        ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
    }
}