package de.factoryfx.data.storage.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileSystemDataStorageHistoryTest {

    private StoredDataMetadata<Void> createStoredDataMetadata(String id){
        return new StoredDataMetadata<>(id,"","","",null,GeneralStorageMetadataBuilder.build(),null);
    }

    @TempDir
    public Path folder;


    private MigrationManager<ExampleDataA,Void> createSerialisation(){
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager((root1, oldDataStorageMetadataDictionary) -> { },ExampleDataA.class), ObjectMapperBuilder.build());
    }


    @Test
    public void test_empty() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        Assertions.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        Assertions.assertEquals(3,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleDataA,Void> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());
        Assertions.assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getHistory() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);

        Assertions.assertNotNull(fileSystemFactoryStorage.getHistoryFactory(new ArrayList<>(fileSystemFactoryStorage.getHistoryFactoryList()).get(0).id));
    }
}