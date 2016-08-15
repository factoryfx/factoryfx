package de.factoryfx.factory.datastorage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemFactoryStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemFactoryStorage<ExampleFactoryA> restored = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()),null,ExampleFactoryA.class);
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        FileSystemFactoryStorage<ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),ExampleFactoryA.class);
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;


        fileSystemFactoryStorage.updateCurrentFactory(new ExampleFactoryA(),"fhdgd");
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());
        Assert.assertEquals(id,new ArrayList<>(fileSystemFactoryStorage.getHistoryFactoryList()).get(0).id);

    }
}

