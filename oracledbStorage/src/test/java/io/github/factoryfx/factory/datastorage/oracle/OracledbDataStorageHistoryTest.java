package io.github.factoryfx.factory.datastorage.oracle;

import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() {
        OracledbDataStorageHistory<ExampleFactoryA> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        Assertions.assertTrue(oracledbDataStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        OracledbDataStorageHistory<ExampleFactoryA> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        StoredDataMetadata metadata = createDummyMetadata();
        oracledbDataStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());
    }

    private StoredDataMetadata createDummyMetadata() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionary = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        return new StoredDataMetadata( UUID.randomUUID().toString(), "", "", "",null,
                dataStorageMetadataDictionary,null);
    }

    @Test
    public void test_multi_add() {
        OracledbDataStorageHistory<ExampleFactoryA> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        {
            oracledbDataStorageHistory.updateHistory(createDummyMetadata(), new ExampleFactoryA());
        }

        {
            oracledbDataStorageHistory.updateHistory(createDummyMetadata(), new ExampleFactoryA());
        }

        {
            oracledbDataStorageHistory.updateHistory(createDummyMetadata(), new ExampleFactoryA());
        }

        assertEquals(3, oracledbDataStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() {
        OracledbDataStorageHistory<ExampleFactoryA> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA> restored = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());
        assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getById() {
        OracledbDataStorageHistory<ExampleFactoryA> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA> history = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        ExampleFactoryA reloaded = oracledbDataStorageHistory.getHistoryFactory(new ArrayList<>(history.getHistoryFactoryList()).get(0).id);
        Assertions.assertNotNull(reloaded);

    }
}