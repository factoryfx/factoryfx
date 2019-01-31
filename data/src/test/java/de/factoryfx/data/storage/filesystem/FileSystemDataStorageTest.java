package de.factoryfx.data.storage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.migration.MigrationManager;

import de.factoryfx.data.storage.NewDataMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemDataStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private MigrationManager<ExampleDataA,Void> createDataMigrationManager(){
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), List.of(), new DataStorageMetadataDictionary(ExampleDataA.class));
    }


    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(), createDataMigrationManager());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(), createDataMigrationManager());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()),null, createDataMigrationManager());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleDataA(), createDataMigrationManager());
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

