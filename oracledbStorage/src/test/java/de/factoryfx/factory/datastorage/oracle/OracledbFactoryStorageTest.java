package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.NewFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashSet;

public class OracledbFactoryStorageTest extends DatabaseTest{

    @Test
    public void test_init_no_existing_factory() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorage = new OracledbFactoryStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,oracledbFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorage = new OracledbFactoryStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();
        String id=oracledbFactoryStorage.getCurrentFactory().metadata.id;

        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> restored = new OracledbFactoryStorage<>(connectionSupplier,null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorage = new OracledbFactoryStorage<>(connectionSupplier, new ExampleFactoryA(),createSerialisation());
        oracledbFactoryStorage.loadInitialFactory();
        String id=oracledbFactoryStorage.getCurrentFactory().metadata.id;

        NewFactoryMetadata metadata = new NewFactoryMetadata();
        FactoryAndNewMetadata<ExampleFactoryA> update = new FactoryAndNewMetadata<>(new ExampleFactoryA(), metadata);
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