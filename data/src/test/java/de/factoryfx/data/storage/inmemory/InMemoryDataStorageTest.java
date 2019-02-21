package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.ScheduledUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InMemoryDataStorageTest {

    private DataUpdate<ExampleDataA> createUpdate() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("update");
        exampleDataA.internal().addBackReferences();
        return new DataUpdate<>(exampleDataA,"user","comment","123");
    }

    public static class Dummy extends Data {

        public final StringAttribute test= new StringAttribute().labelText("fsdsf").defaultValue("1");
    }


    @Test
    public void test_init()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy,Void> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assertions.assertNotNull(fileSystemFactoryStorage.getCurrentData());
    }

    @Test
    public void test_update() {
        InMemoryDataStorage<ExampleDataA,Void> inMemoryDataStorage = new InMemoryDataStorage<>(new ExampleDataA());

        inMemoryDataStorage.getCurrentData();
        Assertions.assertEquals(1,inMemoryDataStorage.getHistoryDataList().size());


        inMemoryDataStorage.updateCurrentData(createUpdate(),null);


        Assertions.assertEquals(2,inMemoryDataStorage.getHistoryDataList().size());
    }

    @Test
    public void test_addFuture()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy,Void> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getHistoryDataList().size());

        ScheduledUpdate<Dummy> update = new ScheduledUpdate<>(
                new Dummy(),
                "user",
                "comment",
                fileSystemFactoryStorage.getCurrentData().id,
                LocalDateTime.now()
        );
        update.root.internal().addBackReferences();

        fileSystemFactoryStorage.addFutureData(update);

        Assertions.assertEquals(1,fileSystemFactoryStorage.getFutureDataList().size());
    }

}