package de.factoryfx.factory.datastorage.filesystem;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.JacksonDeSerialisation;
import de.factoryfx.factory.datastorage.JacksonSerialisation;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemFactoryStorageHistoryTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    private FactorySerialisationManager<ExampleFactoryA> createSerialisation(){
        int dataModelVersion = 1;
        return new FactorySerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleFactoryA.class, dataModelVersion), Collections.emptyList(),1);
    }


    @Test
    public void test_empty() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        Assert.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleFactoryA());

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            fileSystemFactoryStorage.updateHistory(metadata, new ExampleFactoryA());
        }

        Assert.assertEquals(3,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        fileSystemFactoryStorage.initFromFileSystem();

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleFactoryA());
        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleLiveObjectA,Void,ExampleFactoryA> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),createSerialisation());
        restored.initFromFileSystem();
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}