package de.factoryfx.server;

import java.net.MalformedURLException;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryFactoryStorageTest {

    public static class Dummy extends SimpleFactoryBase<DummyLive,Void> {

        public final StringAttribute test= new StringAttribute(new AttributeMetadata().labelText("fsdsf")).defaultValue("1");

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
        InMemoryFactoryStorage<DummyLive,Void,Dummy> fileSystemFactoryStorage = new InMemoryFactoryStorage<>(new Dummy());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertNotNull(fileSystemFactoryStorage.getCurrentFactory());
    }

    @Test
    public void test_update() throws MalformedURLException {
        InMemoryFactoryStorage<DummyLive,Void,Dummy> fileSystemFactoryStorage = new InMemoryFactoryStorage<>(new Dummy());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        FactoryAndStorageMetadata<Dummy> preparedNewFactory = fileSystemFactoryStorage.getPrepareNewFactory();
        fileSystemFactoryStorage.updateCurrentFactory(preparedNewFactory);


        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());
    }

}