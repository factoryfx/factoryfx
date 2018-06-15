package de.factoryfx.data.storage;

import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hbrackmann on 08.05.2017.
 */
public class DataStorageTest {

    @Test
    public void test_getPreviousHistoryFactory() throws InterruptedException {
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("1");
        exampleFactoryA = exampleFactoryA.internal().addBackReferences();

        final InMemoryDataStorage<ExampleDataA,Void> factoryStorage = new InMemoryDataStorage<>(exampleFactoryA);
        factoryStorage.loadInitialFactory();

        Thread.sleep(2);//avoid same timestamp
        {
            DataAndNewMetadata<ExampleDataA> preparedNewFactory = factoryStorage.getPrepareNewFactory();
            preparedNewFactory.root.stringAttribute.set("2");
            factoryStorage.updateCurrentFactory(preparedNewFactory, "", "",null);
        }
        Thread.sleep(2);//avoid same timestamp

        {
            DataAndNewMetadata<ExampleDataA> preparedNewFactory = factoryStorage.getPrepareNewFactory();
            preparedNewFactory.root.stringAttribute.set("3");
            factoryStorage.updateCurrentFactory(preparedNewFactory, "", "",null);
        }
        Thread.sleep(2);//avoid same timestamp

        List<StoredDataMetadata> historyFactoryList = factoryStorage.getHistoryFactoryList().stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        Assert.assertEquals("1",factoryStorage.getHistoryFactory(historyFactoryList.get(0).id).stringAttribute.get());
        Assert.assertEquals("2",factoryStorage.getHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals("3",factoryStorage.getHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("2",factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(2).id).stringAttribute.get());
        Assert.assertEquals("1",factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(1).id).stringAttribute.get());
        Assert.assertEquals(null,factoryStorage.getPreviousHistoryFactory(historyFactoryList.get(0).id));
    }

}