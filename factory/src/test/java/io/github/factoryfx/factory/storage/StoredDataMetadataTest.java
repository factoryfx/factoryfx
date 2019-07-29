package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class StoredDataMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }


    @Test
    public void test_json() {
        LocalDateTime now = LocalDateTime.now();
        StoredDataMetadata value = new StoredDataMetadata(now, "", "", "", "sdfgstrg", new UpdateSummary(new ArrayList<>()), null, null);
        final StoredDataMetadata copy = ObjectMapperBuilder.build().copy(value);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(value));
        Assertions.assertEquals(now, copy.creationTime);
    }


}