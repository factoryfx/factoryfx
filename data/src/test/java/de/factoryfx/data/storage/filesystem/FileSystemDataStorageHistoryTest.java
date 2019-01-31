package de.factoryfx.data.storage.filesystem;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemDataStorageHistoryTest {

    private StoredDataMetadata<Void> createStoredDataMetadata(String id){
        return new StoredDataMetadata<>(id,"","","",null,null,null);
    }

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    private MigrationManager<ExampleDataA,Void> createSerialisation(){
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), List.of(), new DataStorageMetadataDictionary(ExampleDataA.class));
    }


    @Test
    public void test_empty() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        Assert.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleDataA());

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        {
            StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        Assert.assertEquals(3,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredDataMetadata<Void> metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleDataA());
        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleDataA,Void> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        restored.initFromFileSystem();
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}