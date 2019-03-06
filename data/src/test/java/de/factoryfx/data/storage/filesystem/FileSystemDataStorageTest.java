package de.factoryfx.data.storage.filesystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.MigrationManager;

import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileSystemDataStorageTest {

    @TempDir
    public Path folder;

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
        return new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager<>((root1, oldDataStorageMetadataDictionary) -> { },ExampleDataA.class), ObjectMapperBuilder.build());
    }

    @Test
    public void test_init_no_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.getCurrentData();

        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()),null, GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertEquals(id,restored.getCurrentData().id);
    }

    @Test
    public void test_update()  {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;


        DataUpdate<ExampleDataA> update = createUpdate();

        fileSystemFactoryStorage.updateCurrentData(update,null);
        Assertions.assertNotEquals(id,fileSystemFactoryStorage.getCurrentData().id);
        Assertions.assertEquals(2,fileSystemFactoryStorage.getHistoryDataList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryDataList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assertions.assertTrue(ids.contains(id));

    }

    @Test
    public void test_patchCurrentData()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.getCurrentData();//init
        fileSystemFactoryStorage.patchCurrentData((data, metadata) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getCurrentData().root.stringAttribute.get());
    }

    @Test
    public void test_patchAll()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);

        fileSystemFactoryStorage.patchAll((data, metadata) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getHistoryData(id).stringAttribute.get());
    }
}

