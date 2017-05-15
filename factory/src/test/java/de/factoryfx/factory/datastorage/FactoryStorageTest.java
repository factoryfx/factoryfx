package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorageTest;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by hbrackmann on 08.05.2017.
 */
public class FactoryStorageTest {

    @Test
    public void test_getPreviousHistoryFactory() throws InterruptedException {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("1");
        final InMemoryFactoryStorage<ExampleLiveObjectA, Void, ExampleFactoryA> factoryStorage = new InMemoryFactoryStorage<>(exampleFactoryA);
        factoryStorage.loadInitialFactory();

        Thread.sleep(2);//avoid same timestamp
        {
            FactoryAndNewMetadata<ExampleFactoryA> preparedNewFactory = factoryStorage.getPrepareNewFactory();
            preparedNewFactory.root.stringAttribute.set("2");
            factoryStorage.updateCurrentFactory(preparedNewFactory, "", "");
        }
        Thread.sleep(2);//avoid same timestamp

        {
            FactoryAndNewMetadata<ExampleFactoryA> preparedNewFactory = factoryStorage.getPrepareNewFactory();
            preparedNewFactory.root.stringAttribute.set("3");
            factoryStorage.updateCurrentFactory(preparedNewFactory, "", "");
        }
        Thread.sleep(2);//avoid same timestamp

        List<StoredFactoryMetadata> historyFactoryList = factoryStorage.getHistoryFactoryList().stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        Assert.assertEquals("1",factoryStorage.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
        Assert.assertEquals("2",factoryStorage.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals("3",factoryStorage.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("2",factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("1",factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals(null,factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(0).id));
    }

}