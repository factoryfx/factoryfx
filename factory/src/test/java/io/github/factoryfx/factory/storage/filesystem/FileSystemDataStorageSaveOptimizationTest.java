package io.github.factoryfx.factory.storage.filesystem;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemDataStorageSaveOptimizationTest {

    @TempDir
    public Path folder;

    private ExampleDataA createInitialExampleDataA() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();
        return exampleDataA;
    }

    private DataUpdate<ExampleDataA> createUpdate() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("update");
        exampleDataA.internal().finalise();
        return new DataUpdate<>(exampleDataA, "user", "comment", "123");
    }

    private MigrationManager<ExampleDataA> createDataMigrationManager() {
        return new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> {});
    }

    @Test
    public void test_getCurrentDataId_readsOnlyMetadata() throws IOException {
        {
            FileSystemDataStorage<ExampleDataA> storage = new FileSystemDataStorage<>(folder, createInitialExampleDataA(), createDataMigrationManager(), ObjectMapperBuilder.build());
            storage.getCurrentData();//init
        }

        //corrupt the factory json but keep the metadata intact: getCurrentDataId must still work (proves the root is not deserialized)
        String expectedId = ObjectMapperBuilder.build().readTree(Files.readString(folder.resolve("currentFactory_metadata.json"))).get("id").asText();
        Files.writeString(folder.resolve("currentFactory.json"), "garbage - not json");

        FileSystemDataStorage<ExampleDataA> storage = new FileSystemDataStorage<>(folder, createInitialExampleDataA(), createDataMigrationManager(), ObjectMapperBuilder.build());
        Assertions.assertEquals(expectedId, storage.getCurrentDataId());
    }

    @Test
    public void test_currentDataIdCache_correctAcrossUpdates() {
        FileSystemDataStorage<ExampleDataA> storage = new FileSystemDataStorage<>(folder, createInitialExampleDataA(), createDataMigrationManager(), ObjectMapperBuilder.build());
        String initialId = storage.getCurrentData().id;
        Assertions.assertEquals(initialId, storage.getCurrentDataId());

        storage.updateCurrentData(createUpdate(), null);
        String idAfterFirstUpdate = storage.getCurrentDataId();
        Assertions.assertNotEquals(initialId, idAfterFirstUpdate);
        Assertions.assertEquals(storage.getCurrentData().id, idAfterFirstUpdate);

        storage.updateCurrentData(createUpdate(), null);
        Assertions.assertEquals(storage.getCurrentData().id, storage.getCurrentDataId());
    }

    @Test
    public void test_currentAndHistoryContainSameJson() throws IOException {
        FileSystemDataStorage<ExampleDataA> storage = new FileSystemDataStorage<>(folder, createInitialExampleDataA(), createDataMigrationManager(), ObjectMapperBuilder.build());
        storage.getCurrentData();//init
        storage.updateCurrentData(createUpdate(), null);
        String currentId = storage.getCurrentDataId();

        JsonNode current = ObjectMapperBuilder.build().readTree(Files.readString(folder.resolve("currentFactory.json")));
        JsonNode history = ObjectMapperBuilder.build().readTree(Files.readString(folder.resolve("history").resolve(currentId + ".json")));
        Assertions.assertEquals(current, history);

        JsonNode currentMetadata = ObjectMapperBuilder.build().readTree(Files.readString(folder.resolve("currentFactory_metadata.json")));
        JsonNode historyMetadata = ObjectMapperBuilder.build().readTree(Files.readString(folder.resolve("history").resolve(currentId + "_metadata.json")));
        Assertions.assertEquals(currentMetadata, historyMetadata);
    }
}
