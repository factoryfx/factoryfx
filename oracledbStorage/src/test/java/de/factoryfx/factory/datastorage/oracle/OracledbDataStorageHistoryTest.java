package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        Assert.assertTrue(oracledbDataStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        StoredDataMetadata<Void> metadata = createDummyMetadata();
        oracledbDataStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());
    }

    private StoredDataMetadata<Void> createDummyMetadata() {
        GeneralStorageFormat generalStorageFormat = GeneralStorageMetadataBuilder.build();
        DataStorageMetadataDictionary dataStorageMetadataDictionary = new DataStorageMetadataDictionary(Set.of(ExampleFactoryA.class));
        return new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "",null,
                generalStorageFormat,
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
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getById() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA,Void> history = new OracledbDataStorageHistory<>(connectionSupplier, createMigrationManager());

        ExampleFactoryA reloaded = oracledbDataStorageHistory.getHistoryFactory(new ArrayList<>(history.getHistoryFactoryList()).get(0).id);
        Assert.assertNotNull(reloaded);

    }
}