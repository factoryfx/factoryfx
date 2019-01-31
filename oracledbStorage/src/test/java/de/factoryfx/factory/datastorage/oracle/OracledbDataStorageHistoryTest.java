package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

        Assert.assertTrue(oracledbDataStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = createDummyMetadata();
        oracledbDataStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());
    }

    private StoredDataMetadata<Void> createDummyMetadata() {
        return new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "",null,null,null);
    }

    @Test
    public void test_multi_add() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

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
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA,Void> restored = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getById() {
        OracledbDataStorageHistory<ExampleFactoryA,Void> oracledbDataStorageHistory = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

        oracledbDataStorageHistory.updateHistory(createDummyMetadata(),new ExampleFactoryA());
        assertEquals(1, oracledbDataStorageHistory.getHistoryFactoryList().size());

        OracledbDataStorageHistory<ExampleFactoryA,Void> history = new OracledbDataStorageHistory<>(connectionSupplier,createSerialisation());

        ExampleFactoryA reloaded = oracledbDataStorageHistory.getHistoryFactory(new ArrayList<>(history.getHistoryFactoryList()).get(0).id);
        Assert.assertNotNull(reloaded);

    }
}