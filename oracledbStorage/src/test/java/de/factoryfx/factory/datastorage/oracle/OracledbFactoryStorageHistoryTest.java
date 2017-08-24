package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class OracledbFactoryStorageHistoryTest extends DatabaseTest {

    @Test
    public void test_empty() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        Assert.assertTrue(oracledbFactoryStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        assertEquals(3, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());
        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());

        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> restored = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}