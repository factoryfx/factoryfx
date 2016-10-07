package de.factoryfx.factory.datastorage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.UUID;

import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemFactoryStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemFactoryStorage<ExampleLiveObjectA,Void,ExampleFactoryA> restored = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()),null,ExampleFactoryA.class);
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleLiveObjectA,Void,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id = UUID.randomUUID().toString();
        FactoryAndStorageMetadata<ExampleFactoryA> update = new FactoryAndStorageMetadata<>(new ExampleFactoryA(), metadata);
        fileSystemFactoryStorage.updateCurrentFactory(update);
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }
}

