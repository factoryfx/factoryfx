package de.factoryfx.data.storage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.data.storage.JacksonSerialisation;
import de.factoryfx.data.storage.NewDataMetadata;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemDataStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private DataSerialisationManager<ExampleDataA,Void> createSerialisation(){
        int dataModelVersion = 1;
        return new DataSerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleDataA.class, dataModelVersion), Collections.emptyList(),1);
    }


    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()),null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;

        NewDataMetadata metadata = new NewDataMetadata();
        DataAndNewMetadata<ExampleDataA> update = new DataAndNewMetadata<>(new ExampleDataA(), metadata);
        fileSystemFactoryStorage.updateCurrentFactory(update,"","",null);
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }
}

