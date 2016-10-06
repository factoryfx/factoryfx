package de.factoryfx.adminui;

import java.net.MalformedURLException;
import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryFactoryStorageTest {

    public static class Dummy extends FactoryBase<DummyLive,Void>{

        public final StringAttribute test= new StringAttribute(new AttributeMetadata().labelText("fsdsf")).defaultValue("1");

        @Override
        protected DummyLive createImp(Optional<DummyLive> previousLiveObject, LifecycleNotifier<Void> lifecycle) {
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