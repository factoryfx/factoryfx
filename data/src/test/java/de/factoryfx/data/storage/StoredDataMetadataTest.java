package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.MigrationManager;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class StoredDataMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }


    @Test
    public void test_json(){
        LocalDateTime now = LocalDateTime.now();
        StoredDataMetadata<SummaryDummy> value=new StoredDataMetadata<>(now, "", "", "", "sdfgstrg", new SummaryDummy(),new GeneralStorageFormat(1,2),null);
        final StoredDataMetadata<SummaryDummy> copy = ObjectMapperBuilder.build().copy(value);

        System.out.println(
                ObjectMapperBuilder.build().writeValueAsString(value)
        );
        Assert.assertEquals(now,copy.creationTime);
        Assert.assertEquals(1,copy.changeSummary.diffCounter);
        Assert.assertTrue(copy.generalStorageFormat.match(new GeneralStorageFormat(1, 2)));

    }

    @Test
    public void test_compatible_to_old_format(){
        String old =
                "{\n" +
                "  \"creationTime\" : \"2019-01-14T16:54:02.695571\",\n" +
                "  \"baseVersionId\" : \"sdfgstrg\",\n" + "  \"dataModelVersion\" : 0,\n" +
                "  \"changeSummary\" : {\n" +
                "    \"@class\" : \"de.factoryfx.data.storage.StoredDataMetadataTest$SummaryDummy\",\n" +
                "    \"diffCounter\" : 1\n" +
                "  }\n" +
                "}";
        MigrationManager<ExampleDataA,SummaryDummy> manager = new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), List.of(), new DataStorageMetadataDictionary(ExampleDataA.class));
        final StoredDataMetadata<SummaryDummy> oldParsed = manager.readStoredFactoryMetadata(old);


    }


}