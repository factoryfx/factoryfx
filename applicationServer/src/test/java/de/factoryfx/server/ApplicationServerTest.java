package de.factoryfx.server;

import java.util.List;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ApplicationServerTest {

    @Test
    public void test_getDiffForFactory_simple() throws Exception {
        final ExampleFactoryA root = new ExampleFactoryA();
        final InMemoryFactoryStorage<ExampleLiveObjectA, Void, ExampleFactoryA> memoryFactoryStorage = new InMemoryFactoryStorage<>(root);
        memoryFactoryStorage.loadInitialFactory();
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment1");
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment2");
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment3");
        }

        ApplicationServer<ExampleLiveObjectA,Void,ExampleFactoryA> applicationServer = new ApplicationServer<>(Mockito.mock(FactoryManager.class), memoryFactoryStorage);

        final List<AttributeDiffInfo> diff = applicationServer.getDiffForFactory(root.getId());
        Assert.assertEquals(3,diff.size());
        Assert.assertEquals("change3",diff.get(0).newValueValueDisplayText.get().getDisplayText());
        Assert.assertEquals("change2",diff.get(1).newValueValueDisplayText.get().getDisplayText());
        Assert.assertEquals("change1",diff.get(2).newValueValueDisplayText.get().getDisplayText());
    }

    @Test
    public void test_getDiffForFactory_other_change() throws Exception {
        final ExampleFactoryA root = new ExampleFactoryA();
        root.referenceAttribute.set(new ExampleFactoryB());
        final InMemoryFactoryStorage<ExampleLiveObjectA, Void, ExampleFactoryA> memoryFactoryStorage = new InMemoryFactoryStorage<>(root);
        memoryFactoryStorage.loadInitialFactory();
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change1");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment1");
        }
        Thread.sleep(2);//avoid same timestamp
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change2");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment2");
        }
        Thread.sleep(2);//avoid same timestamp
        {//change other factory
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            root.referenceAttribute.get().stringAttribute.set("different change");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment2");
        }

        Thread.sleep(2);//avoid same timestamp
        {
            final FactoryAndNewMetadata<ExampleFactoryA> prepareNewFactory = memoryFactoryStorage.getPrepareNewFactory();
            prepareNewFactory.root.stringAttribute.set("change3");
            memoryFactoryStorage.updateCurrentFactory(prepareNewFactory, "user", "comment3");
        }

        ApplicationServer<ExampleLiveObjectA,Void,ExampleFactoryA> applicationServer = new ApplicationServer<>(Mockito.mock(FactoryManager.class), memoryFactoryStorage);

        final List<AttributeDiffInfo> diff = applicationServer.getDiffForFactory(root.getId());
        Assert.assertEquals(3,diff.size());
        Assert.assertEquals("change3",diff.get(0).newValueValueDisplayText.get().getDisplayText());
        Assert.assertEquals("change2",diff.get(1).newValueValueDisplayText.get().getDisplayText());
        Assert.assertEquals("change1",diff.get(2).newValueValueDisplayText.get().getDisplayText());
    }

}