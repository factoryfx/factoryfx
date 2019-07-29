package io.github.factoryfx.factory.storage.inmemory;

import java.time.LocalDateTime;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.ScheduledUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class InMemoryDataStorageTest {

    private DataUpdate<ExampleDataA> createUpdate() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("update");
        exampleDataA.internal().finalise();
        return new DataUpdate<>(exampleDataA,"user","comment","123");
    }

    public static class Dummy extends FactoryBase<Void, Dummy> {

        public final StringAttribute test= new StringAttribute().labelText("fsdsf").defaultValue("1");
    }


    @Test
    public void test_init()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assertions.assertNotNull(fileSystemFactoryStorage.getCurrentData());
    }

    @Test
    public void test_update() {
        InMemoryDataStorage<ExampleDataA> inMemoryDataStorage = new InMemoryDataStorage<>(new ExampleDataA());

        inMemoryDataStorage.getCurrentData();
        Assertions.assertEquals(1,inMemoryDataStorage.getHistoryDataList().size());


        inMemoryDataStorage.updateCurrentData(createUpdate(),null);


        Assertions.assertEquals(2,inMemoryDataStorage.getHistoryDataList().size());
    }

    @Test
    public void test_addFuture()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryDataList().size());

        ScheduledUpdate<Dummy> update = new ScheduledUpdate<>(
                new Dummy(),
                "user",
                "comment",
                fileSystemFactoryStorage.getCurrentData().id,
                LocalDateTime.now()
        );
        update.root.internal().finalise();

        fileSystemFactoryStorage.addFutureData(update);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getFutureDataList().size());
    }

}