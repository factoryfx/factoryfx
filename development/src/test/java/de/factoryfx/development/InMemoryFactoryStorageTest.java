package de.factoryfx.development;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.StringAttribute;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.server.DefaultApplicationServer;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryFactoryStorageTest {

    public static class Dummy extends FactoryBase<DummyLive,Dummy>{

        public final StringAttribute test= new StringAttribute(new AttributeMetadata().labelText("fsdsf")).defaultValue("1");

        @Override
        protected DummyLive createImp(Optional<DummyLive> previousLiveObject) {
            return new DummyLive(test.get());
        }
    }

    public static class DummyLive implements LiveObject<Object> {
        private final String test;

        public DummyLive(String test) {
            this.test=test;
        }

        @Override
        public void start() {
            calls.add(test);
        }

        @Override
        public void stop() {

        }

        @Override
        public void accept(Object visitor) {

        }
    }

    static List<String> calls = new ArrayList<>();

    @Test
    public void test(){
        DefaultApplicationServer<Object,Dummy> applicationServer = new DefaultApplicationServer<>(new FactoryManager<>(),new InMemoryFactoryStorage<>(new Dummy()));
        applicationServer.start();

        FactoryAndStorageMetadata<Dummy> currentFactory = applicationServer.getCurrentFactory();
        currentFactory.root.test.set("2");

        applicationServer.updateCurrentFactory(currentFactory.root, currentFactory.metadata.baseVersionId, Locale.ENGLISH, "fdsag");
        Assert.assertEquals(2,calls.size());
        Assert.assertEquals("1",calls.get(0));
        Assert.assertEquals("2",calls.get(1));
    }

}