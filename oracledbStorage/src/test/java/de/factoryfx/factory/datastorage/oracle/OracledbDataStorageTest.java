package de.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class OracledbDataStorageTest extends DatabaseTest{

    private ExampleFactoryA createInitialExampleFactoryA() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("initial");
        exampleFactoryA.internal().addBackReferences();
        return exampleFactoryA;
    }

    private DataUpdate<ExampleFactoryA> createUpdate() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("update");
        exampleFactoryA.internal().addBackReferences();
        return new DataUpdate<>(exampleFactoryA,"user","comment","123");
    }

    @Test
    public void test_init_no_existing_factory() {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());
        oracledbFactoryStorage.getCurrentData();

        Assertions.assertEquals(1,oracledbFactoryStorage.getHistoryDataList().size());
    }

    @Test
    public void test_init_existing_factory() {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());
        String id=oracledbFactoryStorage.getCurrentData().id;

        OracledbDataStorage<ExampleFactoryA,Void> restored = new OracledbDataStorage<>(connectionSupplier,null, GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());
        Assertions.assertEquals(id,restored.getCurrentData().id);
    }

    @Test
    public void test_update()  {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());
        oracledbFactoryStorage.getCurrentData();//usually called in preparenew
        Assertions.assertEquals(1,oracledbFactoryStorage.getHistoryDataList().size());
        oracledbFactoryStorage.updateCurrentData(createUpdate(),null);
        Assertions.assertEquals(2,oracledbFactoryStorage.getHistoryDataList().size());

        StoredDataMetadata<Void> storedDataMetadata = new ArrayList<>(oracledbFactoryStorage.getHistoryDataList()).get(1);
        Assertions.assertEquals("update", oracledbFactoryStorage.getHistoryData(storedDataMetadata.id).stringAttribute.get());
        Assertions.assertEquals("update", oracledbFactoryStorage.getCurrentData().root.stringAttribute.get());
        StoredDataMetadata<Void> storedDataMetadataFirst = new ArrayList<>(oracledbFactoryStorage.getHistoryDataList()).get(0);
        Assertions.assertEquals("initial", oracledbFactoryStorage.getHistoryData(storedDataMetadataFirst.id).stringAttribute.get());
    }

    @Test
    public void test_patchCurrentData()  {
        ExampleFactoryA initialExampleDataA = createInitialExampleFactoryA();
        initialExampleDataA.stringAttribute.set("123");
        OracledbDataStorage<ExampleFactoryA,Void> oracleStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());
        oracleStorage.getCurrentData();//init
        oracleStorage.patchCurrentData((data, metadata) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",oracleStorage.getCurrentData().root.stringAttribute.get());
    }


    @Test
    public void test_patchAll()  {
        ExampleFactoryA initialExampleDataA = createInitialExampleFactoryA();
        initialExampleDataA.stringAttribute.set("123");
        OracledbDataStorage<ExampleFactoryA,Void> oracleStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager(), ObjectMapperBuilder.build());String id=oracleStorage.getCurrentData().id;
        oracleStorage.updateCurrentData(createUpdate(),null);

        oracleStorage.patchAll((data, metadata) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",oracleStorage.getHistoryData(id).stringAttribute.get());
    }


}