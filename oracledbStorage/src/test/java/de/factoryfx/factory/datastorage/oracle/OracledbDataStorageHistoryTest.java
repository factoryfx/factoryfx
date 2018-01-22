package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        Assert.assertTrue(oracledbFactoryStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        {
            StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        assertEquals(3, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());
        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());

        OracledbFactoryStorageHistory<ExampleFactoryA,Void> restored = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_getById() {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());
        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());

        OracledbFactoryStorageHistory<ExampleFactoryA,Void> history = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        ExampleFactoryA reloaded = oracledbFactoryStorageHistory.getHistoryFactory(new ArrayList<>(history.getHistoryFactoryList()).get(0).id);
        Assert.assertNotNull(reloaded);

    }
}