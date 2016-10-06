package de.factoryfx.factory.datastorage.postgres;

import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.datastorage.filesystem.FileSystemFactoryStorageHistory;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.UUID;

public class FileSystemFactoryStorageHistoryTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    @Test
    public void test_empty() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),ExampleFactoryA.class);
        fileSystemFactoryStorage.initFromFileSystem();

        Assert.assertTrue(fileSystemFactoryStorage.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),ExampleFactoryA.class);
        fileSystemFactoryStorage.initFromFileSystem();

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleFactoryA());

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
        FileSystemFactoryStorageHistory<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),ExampleFactoryA.class);
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
        FileSystemFactoryStorageHistory<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),ExampleFactoryA.class);
        fileSystemFactoryStorage.initFromFileSystem();

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        fileSystemFactoryStorage.updateHistory(metadata,new ExampleFactoryA());
        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FileSystemFactoryStorageHistory<ExampleFactoryA> restored = new FileSystemFactoryStorageHistory<>(Paths.get(folder.getRoot().toURI()),ExampleFactoryA.class);
        restored.initFromFileSystem();
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}