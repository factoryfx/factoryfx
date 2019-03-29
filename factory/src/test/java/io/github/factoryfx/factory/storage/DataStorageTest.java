package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.inmemory.InMemoryDataStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Thread.sleep(2);//avoid same timestamp
        {
            DataAndId<ExampleDataA> currentFactory = factoryStorage.getCurrentData();
            ExampleDataA preparedNewFactory = currentFactory.root.utility().copy();
            preparedNewFactory.stringAttribute.set("2");
            factoryStorage.updateCurrentData(new DataUpdate<>(preparedNewFactory, "user","comment",currentFactory.id),null);
        }
        Thread.sleep(2);//avoid same timestamp

        {
            DataAndId<ExampleDataA> currentFactory = factoryStorage.getCurrentData();
            ExampleDataA preparedNewFactory = currentFactory.root.utility().copy();
            preparedNewFactory.stringAttribute.set("3");
            factoryStorage.updateCurrentData(new DataUpdate<>(preparedNewFactory, "user","comment",currentFactory.id),null);
        }
        Thread.sleep(2);//avoid same timestamp

        List<StoredDataMetadata> historyFactoryList = factoryStorage.getHistoryDataList().stream().sorted(Comparator.comparing(h -> h.creationTime)).collect(Collectors.toList());
        Assertions.assertEquals("1",factoryStorage.getHistoryData(historyFactoryList.get(0).id).stringAttribute.get());
        Assertions.assertEquals("2",factoryStorage.getHistoryData(historyFactoryList.get(1).id).stringAttribute.get());
        Assertions.assertEquals("3",factoryStorage.getHistoryData(historyFactoryList.get(2).id).stringAttribute.get());
        Assertions.assertEquals("2",factoryStorage.getPreviousHistoryData(historyFactoryList.get(2).id).stringAttribute.get());
        Assertions.assertEquals("1",factoryStorage.getPreviousHistoryData(historyFactoryList.get(1).id).stringAttribute.get());
        Assertions.assertEquals(null,factoryStorage.getPreviousHistoryData(historyFactoryList.get(0).id));
    }

}