package de.factoryfx.data.storage.filesystem;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.data.storage.JacksonSerialisation;
import de.factoryfx.data.storage.StoredDataMetadata;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemDataStorageHistoryTest {

    private StoredDataMetadata<Void> createStoredDataMetadata(String id){
        return new StoredDataMetadata<>(id,"","","",0,null);
    }

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    private DataSerialisationManager<ExampleDataA,Void> createSerialisation(){
        int dataModelVersion = 1;
        return new DataSerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleDataA.class, dataModelVersion), Collections.emptyList(),1);
    }


    @Test
    public void test_empty() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        Assert.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredDataMetadata metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleDataA());

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        {
            StoredDataMetadata metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        {
            StoredDataMetadata metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        {
            StoredDataMetadata metadata = createStoredDataMetadata(UUID.randomUUID().toString());
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleDataA());
        }

        Assert.assertEquals(3,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredDataMetadata metadata = createStoredDataMetadata(UUID.randomUUID().toString());
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleDataA());
        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleDataA,Void> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        restored.initFromFileSystem();
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}