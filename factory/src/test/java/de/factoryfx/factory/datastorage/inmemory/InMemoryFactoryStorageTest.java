package de.factoryfx.factory.datastorage.inmemory;

import java.net.MalformedURLException;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryFactoryStorageTest {

    public static class Dummy extends SimpleFactoryBase<DummyLive,Void> {

        public final StringAttribute test= new StringAttribute().labelText("fsdsf").defaultValue("1");

        @Override
        public DummyLive createImpl() {
            return new DummyLive(test.get());
        }

    }

    public static class DummyLive {
        private final String test;

        public DummyLive(String test) {
            this.test=test;
        }

    }

    @Test
    public void test_init() throws MalformedURLException {
        InMemoryFactoryStorage<Void,DummyLive,Dummy> fileSystemFactoryStorage = new InMemoryFactoryStorage<>(new Dummy());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertNotNull(fileSystemFactoryStorage.getCurrentFactory());
    }

    @Test
    public void test_update() throws MalformedURLException {
        InMemoryFactoryStorage<Void,DummyLive,Dummy> fileSystemFactoryStorage = new InMemoryFactoryStorage<>(new Dummy());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FactoryAndNewMetadata<Dummy> preparedNewFactory = fileSystemFactoryStorage.getPrepareNewFactory();
        fileSystemFactoryStorage.updateCurrentFactory(preparedNewFactory,"","");


        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

}