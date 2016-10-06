package de.factoryfx.adminui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LifecycleNotifier;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.server.DefaultApplicationServer;
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

    static List<String> calls = new ArrayList<>();

    @Test
    public void test(){
        DefaultApplicationServer<DummyLive,Void,Dummy> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(),new InMemoryFactoryStorage<>(new Dummy()));
        applicationServer.start();

        FactoryAndStorageMetadata<Dummy> currentFactory = applicationServer.getCurrentFactory();
        currentFactory.root.test.set("2");

        applicationServer.updateCurrentFactory(new FactoryAndStorageMetadata<>(currentFactory.root,currentFactory.metadata), Locale.ENGLISH);
        Assert.assertEquals(2,calls.size());
        Assert.assertEquals("1",calls.get(0));
        Assert.assertEquals("2",calls.get(1));
    }

}