package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class ApplicationFactoryMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }

    @Test
    public void test_json(){
        StoredDataMetadata<SummaryDummy> storedDataMetadata = new StoredDataMetadata<SummaryDummy>("","","","",new SummaryDummy(),null,null);
        ObjectMapperBuilder.build().copy(storedDataMetadata); //test json serializable
    }

}