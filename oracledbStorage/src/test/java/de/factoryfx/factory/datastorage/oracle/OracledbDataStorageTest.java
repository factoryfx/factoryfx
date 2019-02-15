package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
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
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager());
        oracledbFactoryStorage.getCurrentFactory();

        Assert.assertEquals(1,oracledbFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_init_existing_factory() {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager());
        String id=oracledbFactoryStorage.getCurrentFactory().id;

        OracledbDataStorage<ExampleFactoryA,Void> restored = new OracledbDataStorage<>(connectionSupplier,null, GeneralStorageMetadataBuilder.build(), createMigrationManager());
        Assert.assertEquals(id,restored.getCurrentFactory().id);
    }

    @Test
    public void test_update()  {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), GeneralStorageMetadataBuilder.build(), createMigrationManager());
        oracledbFactoryStorage.getCurrentFactory();//usually called in preparenew
        Assert.assertEquals(1,oracledbFactoryStorage.getHistoryFactoryList().size());
        oracledbFactoryStorage.updateCurrentFactory(createUpdate(),null);
        Assert.assertEquals(2,oracledbFactoryStorage.getHistoryFactoryList().size());

        StoredDataMetadata<Void> storedDataMetadata = new ArrayList<>(oracledbFactoryStorage.getHistoryFactoryList()).get(1);
        Assert.assertEquals("update", oracledbFactoryStorage.getHistoryFactory(storedDataMetadata.id).stringAttribute.get());
        Assert.assertEquals("update", oracledbFactoryStorage.getCurrentFactory().root.stringAttribute.get());
        StoredDataMetadata<Void> storedDataMetadataFirst = new ArrayList<>(oracledbFactoryStorage.getHistoryFactoryList()).get(0);
        Assert.assertEquals("initial", oracledbFactoryStorage.getHistoryFactory(storedDataMetadataFirst.id).stringAttribute.get());
    }

}