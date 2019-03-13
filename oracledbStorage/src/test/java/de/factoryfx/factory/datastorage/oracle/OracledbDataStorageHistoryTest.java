package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        Assertions.assertTrue(oracledbDataStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        StoredDataMetadata<Void> metadata = createDummyMetadata();
        oracledbDataStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());
    }

    private StoredDataMetadata<Void> createDummyMetadata() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionary = exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot();
        return new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "",null,
                dataStorageMetadataDictionary);
    }

    @Test
    public void test_multi_add() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

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
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA,Void> restored = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());
        assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getById() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA,Void> history = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        ExampleFactoryA reloaded = oracledbDataStorageHistory.getHistoryFactory(new ArrayList<>(history.getHistoryFactoryList()).get(0).id);
        Assertions.assertNotNull(reloaded);

    }
}