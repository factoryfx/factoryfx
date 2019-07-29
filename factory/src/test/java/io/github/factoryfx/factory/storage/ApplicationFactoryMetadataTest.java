package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ApplicationFactoryMetadataTest {

    @Test
    public void test_json(){
        StoredDataMetadata storedDataMetadata = new StoredDataMetadata("","","","",new UpdateSummary(new ArrayList<>()),null,null);
        ObjectMapperBuilder.build().copy(storedDataMetadata); //test json serializable
    }

}