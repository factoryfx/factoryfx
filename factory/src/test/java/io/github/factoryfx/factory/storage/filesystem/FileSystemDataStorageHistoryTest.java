package io.github.factoryfx.factory.storage.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.filesystem.FileSystemFactoryStorageHistory;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileSystemDataStorageHistoryTest {

    private StoredDataMetadata<Void> createDummyStoredDataMetadata(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        return new StoredDataMetadata<>(UUID.randomUUID().toString(),"","","",null, dataStorageMetadataDictionaryFromRoot,null);
    }

    @TempDir
    public Path folder;


    private MigrationManager<ExampleDataA,Void> createSerialisation(){
        return new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
    }


    @Test
    public void test_empty() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        Assertions.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        {
            StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        {
            StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        {
            StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
            fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        }

        Assertions.assertEquals(3,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);
        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleDataA,Void> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());
        Assertions.assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getHistory() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.toFile().toURI()),createSerialisation());

        StoredDataMetadata<Void> metadata = createDummyStoredDataMetadata();
        fileSystemFactoryStorage.updateHistory(new ExampleDataA(), metadata);

        Assertions.assertNotNull(fileSystemFactoryStorage.getHistoryFactory(new ArrayList<>(fileSystemFactoryStorage.getHistoryFactoryList()).get(0).id));
    }
}