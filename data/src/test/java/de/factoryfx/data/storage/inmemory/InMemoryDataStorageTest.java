package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;
import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.ScheduledUpdate;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertNotNull(fileSystemFactoryStorage.getCurrentFactory());
    }

    @Test
    public void test_update() {
        InMemoryDataStorage<ExampleDataA,Void> inMemoryDataStorage = new InMemoryDataStorage<>(new ExampleDataA());

        inMemoryDataStorage.getCurrentFactory();
        Assert.assertEquals(1,inMemoryDataStorage.getHistoryFactoryList().size());


        inMemoryDataStorage.updateCurrentFactory(createUpdate(),null);


        Assert.assertEquals(2,inMemoryDataStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_addFuture()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy,Void> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        ScheduledUpdate<Dummy> update = new ScheduledUpdate<>(
                new Dummy(),
                "user",
                "comment",
                fileSystemFactoryStorage.getCurrentFactory().id,
                LocalDateTime.now()
        );
        update.root.internal().addBackReferences();

        fileSystemFactoryStorage.addFutureFactory(update);

        Assert.assertEquals(1,fileSystemFactoryStorage.getFutureFactoryList().size());
    }

}