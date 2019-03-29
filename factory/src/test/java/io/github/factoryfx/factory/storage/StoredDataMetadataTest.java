package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class StoredDataMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }


    @Test
    public void test_json(){
        LocalDateTime now = LocalDateTime.now();
        StoredDataMetadata<SummaryDummy> value=new StoredDataMetadata<>(now, "", "", "", "sdfgstrg", new SummaryDummy(),null);
        final StoredDataMetadata<SummaryDummy> copy = ObjectMapperBuilder.build().copy(value);

        System.out.println(
                ObjectMapperBuilder.build().writeValueAsString(value)
        );
        Assertions.assertEquals(now,copy.creationTime);
        Assertions.assertEquals(1,copy.changeSummary.diffCounter);
    }

    @Test
    public void test_compatible_to_old_format(){
        String old =
                "{\n" +
                "  \"creationTime\" : \"2019-01-14T16:54:02.695571\",\n" +
                "  \"baseVersionId\" : \"sdfgstrg\",\n" + "  \"dataModelVersion\" : 0,\n" +
                "  \"changeSummary\" : {\n" +
                "    \"@class\" : \"io.github.factoryfx.factory.storage.StoredDataMetadataTest$SummaryDummy\",\n" +
                "    \"diffCounter\" : 1\n" +
                "  }\n" +
                "}";
        MigrationManager<ExampleDataA,SummaryDummy> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), ((root1, oldDataStorageMetadataDictionary) -> { }));
        Assertions.assertNotNull(manager.readStoredFactoryMetadata(old));


    }


}