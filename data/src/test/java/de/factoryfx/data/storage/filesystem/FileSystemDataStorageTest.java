package de.factoryfx.data.storage.filesystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.data.storage.migration.MigrationManager;

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
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), List.of());
    }


    @Test
    public void test_init_no_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), createDataMigrationManager());
        fileSystemFactoryStorage.getCurrentFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    private DataAndStoredMetadata<ExampleDataA,Void> createInitialExampleDataA() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        GeneralStorageFormat generalStorageFormat = GeneralStorageMetadataBuilder.build();
        DataAndStoredMetadata<ExampleDataA,Void> initialFactoryAndStorageMetadata = new DataAndStoredMetadata<>(exampleDataA,
                new StoredDataMetadata<>(LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        "System",
                        "initial factory",
                        UUID.randomUUID().toString(),
                        null,
                        generalStorageFormat,
                        exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot()
                )
        );
        return initialFactoryAndStorageMetadata;
    }

    @Test
    public void test_init_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), createDataMigrationManager());
        String id=fileSystemFactoryStorage.getCurrentFactory().id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()),null, createDataMigrationManager());
        Assert.assertEquals(id,restored.getCurrentFactory().id);
    }

    @Test
    public void test_update()  {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.getRoot().toURI()), createInitialExampleDataA(), createDataMigrationManager());
        String id=fileSystemFactoryStorage.getCurrentFactory().id;


        DataAndStoredMetadata<ExampleDataA,Void> update = createInitialExampleDataA();

        fileSystemFactoryStorage.updateCurrentFactory(update);
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }
}

