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
        exampleFactoryA = exampleFactoryA.internal().finalise();

        final InMemoryDataStorage<ExampleDataA,Void> factoryStorage = new InMemoryDataStorage<>(exampleFactoryA);

        {
            DataAndId<ExampleDataA> currentFactory = factoryStorage.getCurrentData();
            ExampleDataA preparedNewFactory = currentFactory.root.utility().copy();
            preparedNewFactory.stringAttribute.set("2");
            factoryStorage.updateCurrentData(new DataUpdate<>(preparedNewFactory, "user","xa",currentFactory.id),null);
        }

        {
            DataAndId<ExampleDataA> currentFactory = factoryStorage.getCurrentData();
            ExampleDataA preparedNewFactory = currentFactory.root.utility().copy();
            preparedNewFactory.stringAttribute.set("3");
            factoryStorage.updateCurrentData(new DataUpdate<>(preparedNewFactory, "user","xb",currentFactory.id),null);
        }

        List<StoredDataMetadata> historyFactoryList = factoryStorage.getHistoryDataList().stream().sorted(Comparator.comparing(h -> h.comment)).collect(Collectors.toList());
        Assertions.assertEquals("1",factoryStorage.getHistoryData(historyFactoryList.get(0).id).stringAttribute.get());
        Assertions.assertEquals("2",factoryStorage.getHistoryData(historyFactoryList.get(1).id).stringAttribute.get());
        Assertions.assertEquals("3",factoryStorage.getHistoryData(historyFactoryList.get(2).id).stringAttribute.get());
        Assertions.assertEquals("2",factoryStorage.getHistoryData(historyFactoryList.get(2).mergerVersionId).stringAttribute.get());
        Assertions.assertEquals("1",factoryStorage.getHistoryData(historyFactoryList.get(1).mergerVersionId).stringAttribute.get());
        Assertions.assertEquals(null,historyFactoryList.get(0).mergerVersionId);
    }

}