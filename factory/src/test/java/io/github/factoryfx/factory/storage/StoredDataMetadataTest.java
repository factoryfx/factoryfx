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

    @Test
    public void test_legacy_metadata_without_configurationSchemaVersion_parses() {
        String legacyJson = """
                {
                  "creationTime" : "2020-01-01T10:00:00",
                  "id" : "id1",
                  "user" : "user",
                  "comment" : "comment",
                  "baseVersionId" : "base1",
                  "mergerVersionId" : "merger1"
                }""";

        StoredDataMetadata metadata = ObjectMapperBuilder.build().readValue(legacyJson, StoredDataMetadata.class);
        Assertions.assertEquals("id1", metadata.id);
        Assertions.assertNull(metadata.configurationSchemaVersion);

        StoredDataMetadata lightMetadata = StoredDataMetadata.createLightStoredDataMetadata(ObjectMapperBuilder.build().readTree(legacyJson));
        Assertions.assertEquals("id1", lightMetadata.id);
        Assertions.assertNull(lightMetadata.configurationSchemaVersion);
    }

    @Test
    public void test_roundtrip_with_configurationSchemaVersion() {
        StoredDataMetadata metadata = new StoredDataMetadata("id1", "user", "comment", "base1", null, null, "merger1", 3);
        String json = ObjectMapperBuilder.build().writeValueAsString(metadata);

        StoredDataMetadata parsed = ObjectMapperBuilder.build().readValue(json, StoredDataMetadata.class);
        Assertions.assertEquals(3, parsed.configurationSchemaVersion);

        StoredDataMetadata lightParsed = StoredDataMetadata.createLightStoredDataMetadata(ObjectMapperBuilder.build().readTree(json));
        Assertions.assertEquals(3, lightParsed.configurationSchemaVersion);
    }

    @Test
    public void test_light_copy_keeps_configurationSchemaVersion() {
        StoredDataMetadata metadata = new StoredDataMetadata("id1", "user", "comment", "base1", null, null, "merger1", 7);
        StoredDataMetadata light = StoredDataMetadata.createLightStoredDataMetadata(metadata);
        Assertions.assertEquals(7, light.configurationSchemaVersion);
        Assertions.assertNull(light.changeSummary);
        Assertions.assertNull(light.dataStorageMetadataDictionary);
    }

}