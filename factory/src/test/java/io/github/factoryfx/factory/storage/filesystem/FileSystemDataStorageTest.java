package io.github.factoryfx.factory.storage.filesystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;
import io.github.factoryfx.jetty.AllExceptionMapper;
import io.github.factoryfx.jetty.DefaultObjectMapper;
import io.github.factoryfx.jetty.JerseyServletFactory;
import io.github.factoryfx.jetty.Slf4LoggingFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileSystemDataStorageTest {

    @TempDir
    public Path folder;

    private ExampleDataA createInitialExampleDataA() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();
        return exampleDataA;
    }

    private DataUpdate<ExampleDataA> createUpdate() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("update");
        exampleDataA.internal().finalise();
        return new DataUpdate<>(exampleDataA,"user","comment","123");
    }


    private MigrationManager<ExampleDataA> createDataMigrationManager(){
        return new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
    }

    @Test
    public void test_init_no_existing_factory() {
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.getCurrentData();

        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() {
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        Assertions.assertTrue(new File(folder.toFile().getAbsolutePath()+"/currentFactory.json").exists());

        FileSystemDataStorage<ExampleDataA> restored = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()),null, createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertEquals(id,restored.getCurrentData().id);
    }

    @Test
    public void test_update()  {
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), createInitialExampleDataA(), createDataMigrationManager(),ObjectMapperBuilder.build());
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
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.getCurrentData();//init
        fileSystemFactoryStorage.patchCurrentData((root, metadata, objectMapper) -> {
            ((ObjectNode) root.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getCurrentData().root.stringAttribute.get());
    }

    @Test
    public void test_patchAll()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);

        fileSystemFactoryStorage.patchAll((data, metadata,objectMapper) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getHistoryData(id).stringAttribute.get());
    }

    @Test
    public void test_patchAll2()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=fileSystemFactoryStorage.getCurrentData().id;
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);

        fileSystemFactoryStorage.patchAll((root, metadata, objectMapper) -> {
            DataJsonNode rootNode = new DataJsonNode(root);
            for (DataJsonNode dataJsonNode : rootNode.collectChildrenFromRoot()) {
                if (dataJsonNode.getDataClassName().equals(ExampleDataA.class.getName())) {
                    dataJsonNode.setAttributeValue("stringAttribute",new TextNode("qqq"));
                }
            }
        });

        Assertions.assertEquals("qqq",fileSystemFactoryStorage.getHistoryData(id).stringAttribute.get());
    }


    @Test
    public void test_getInitialFactory()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");

        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build());
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);
        fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);

        Assertions.assertEquals("123",fileSystemFactoryStorage.getInitialData().stringAttribute.get());
    }

    @Test
    public void test_getInitialFactory_housekeeping()  {
        ExampleDataA initialExampleDataA = createInitialExampleDataA();
        initialExampleDataA.stringAttribute.set("123");

        int maxConfigurationHistory = 5;
        FileSystemDataStorage<ExampleDataA> fileSystemFactoryStorage = new FileSystemDataStorage<>(Paths.get(folder.toFile().toURI()), initialExampleDataA, createDataMigrationManager(),ObjectMapperBuilder.build(), maxConfigurationHistory);
        for (int i = 0; i < maxConfigurationHistory+1; i++) {
            fileSystemFactoryStorage.updateCurrentData(createUpdate(),null);
        }

        Assertions.assertEquals("123",fileSystemFactoryStorage.getInitialData().stringAttribute.get());
    }

}

