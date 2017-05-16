package de.factoryfx.factory.datastorage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;

import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.JacksonDeSerialisation;
import de.factoryfx.factory.datastorage.JacksonSerialisation;
import de.factoryfx.factory.datastorage.NewFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemFactoryStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private FactorySerialisationManager<ExampleFactoryA> createSerialisation(){
        int dataModelVersion = 1;
        return new FactorySerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleFactoryA.class, dataModelVersion), Collections.emptyList(),1);
    }


    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        FileSystemFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> restored = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()),null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        FileSystemFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;

        NewFactoryMetadata metadata = new NewFactoryMetadata();
        FactoryAndNewMetadata<ExampleFactoryA> update = new FactoryAndNewMetadata<>(new ExampleFactoryA(), metadata);
        fileSystemFactoryStorage.updateCurrentFactory(update,"","");
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }
}

