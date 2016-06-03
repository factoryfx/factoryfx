package de.factoryfx.factory.merge;

import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class MergeResultEntryTest {


    @Test
    public void json_serialisable(){
        MergeResultEntry  mergeResultEntry = new MergeResultEntry(null,null,"11","22");
        ObjectMapperBuilder.build().copy(mergeResultEntry);//Test json serialisation
    }
}