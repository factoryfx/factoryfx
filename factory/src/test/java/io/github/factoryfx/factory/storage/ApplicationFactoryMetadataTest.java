package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

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