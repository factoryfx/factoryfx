package de.factoryfx.data.storage.filesystem;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.MigrationManager;

import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemDataStorageTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private ExampleDataA createInitialExampleDataA() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        return exampleDataA;
    }

    private DataUpdate<ExampleDataA> createUpdate() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("update");
        exampleDataA.internal().addBackReferences();
        return new DataUpdate<>(exampleDataA,"user","comment","123");
    }


    private MigrationManager<ExampleDataA,Void> createDataMigrationManager(){
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager(), ObjectMapperBuilder.build());
    }

    @Test
    public void test_init_no_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        fileSystemFactoryStorage.getCurrentFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        String id=fileSystemFactoryStorage.getCurrentFactory().id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()),null, GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        Assert.assertEquals(id,restored.getCurrentFactory().id);
    }

    @Test
    public void test_update()  {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        String id=fileSystemFactoryStorage.getCurrentFactory().id;


        DataUpdate<ExampleDataA> update = createUpdate();

        fileSystemFactoryStorage.updateCurrentFactory(update,null);
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }
}

