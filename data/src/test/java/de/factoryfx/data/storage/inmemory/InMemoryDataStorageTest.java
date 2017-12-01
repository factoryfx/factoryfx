package de.factoryfx.data.storage.inmemory;

import java.net.MalformedURLException;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.storage.DataAndNewMetadata;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryDataStorageTest {

    public static class Dummy extends Data {

        public final StringAttribute test= new StringAttribute().labelText("fsdsf").defaultValue("1");


    }



    @Test
    public void test_init() throws MalformedURLException {
        Dummy dummy = new Dummy();
        dummy = dummy.internal().prepareUsableCopy();
        InMemoryDataStorage<Dummy> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertNotNull(fileSystemFactoryStorage.getCurrentFactory());
    }

    @Test
    public void test_update() throws MalformedURLException {
        Dummy dummy = new Dummy();
        dummy = dummy.internal().prepareUsableCopy();
        InMemoryDataStorage<Dummy> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        DataAndNewMetadata<Dummy> preparedNewFactory = fileSystemFactoryStorage.getPrepareNewFactory();
        fileSystemFactoryStorage.updateCurrentFactory(preparedNewFactory,"","");


        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

}