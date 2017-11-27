package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.NewDataMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashSet;

public class OracledbDataStorageTest extends DatabaseTest{

    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        OracledbDataStorage<ExampleFactoryA> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,oracledbFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        OracledbDataStorage<ExampleFactoryA> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();
        String id=oracledbFactoryStorage.getCurrentFactory().metadata.id;

        OracledbDataStorage<ExampleFactoryA> restored = new OracledbDataStorage<>(connectionSupplier,null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        OracledbDataStorage<ExampleFactoryA> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();
        String id=oracledbFactoryStorage.getCurrentFactory().metadata.id;

        NewDataMetadata metadata = new NewDataMetadata();
        DataAndNewMetadata<ExampleFactoryA> update = new DataAndNewMetadata<>(new ExampleFactoryA(), metadata);
        oracledbFactoryStorage.updateCurrentFactory(update,"","");
        Assert.assertNotEquals(id,oracledbFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,oracledbFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        oracledbFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }

}