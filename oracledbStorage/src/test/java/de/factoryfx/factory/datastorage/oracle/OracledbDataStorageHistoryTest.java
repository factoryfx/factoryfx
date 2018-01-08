package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class OracledbDataStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() throws MalformedURLException {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        Assert.assertTrue(oracledbFactoryStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
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
    public void test_restore() throws MalformedURLException {
        OracledbFactoryStorageHistory<ExampleFactoryA,Void> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredDataMetadata<Void> metadata = new StoredDataMetadata<>( UUID.randomUUID().toString(), "", "", "", 0,null);
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());
        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());

        OracledbFactoryStorageHistory<ExampleFactoryA,Void> restored = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}