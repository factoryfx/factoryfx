package io.github.factoryfx.factory.storage.filesystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.filesystem.FileSystemDataStorage;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

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
        return new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
    }

    @Test
    public void test_init_no_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.getCurrentData();

        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA,Void> restored = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()),null, createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertEquals(id,restored.getCurrentData().id);
    }

    @Test
    public void test_update()  {
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
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
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
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
        FileSystemDataStorage<ExampleDataA,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);

        fileSystemFactoryStorage.patchAll((data, metadata) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getHistoryData(id).stringAttribute.get());
    }
}

