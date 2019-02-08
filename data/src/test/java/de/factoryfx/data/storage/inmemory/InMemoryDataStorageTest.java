package de.factoryfx.data.storage.inmemory;

import java.time.LocalDateTime;
import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.storage.DataAndScheduledMetadata;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryDataStorageTest {

    private DataAndStoredMetadata<Dummy,Void> createInitialFactory() {
        Dummy exampleFactoryA = new Dummy();
        exampleFactoryA.internal().addBackReferences();
        GeneralStorageFormat generalStorageFormat = GeneralStorageMetadataBuilder.build();
        DataAndStoredMetadata<Dummy,Void> initialFactoryAndStorageMetadata = new DataAndStoredMetadata<>(exampleFactoryA,
                new StoredDataMetadata<>(LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        "System",
                        "initial factory",
                        UUID.randomUUID().toString(),
                        null,
                        generalStorageFormat,
                        exampleFactoryA.internal().createDataStorageMetadataDictionaryFromRoot()
                )
        );
        return initialFactoryAndStorageMetadata;
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
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy,Void> inMemoryDataStorage = new InMemoryDataStorage<>(dummy);

        inMemoryDataStorage.getCurrentFactory();
        Assert.assertEquals(1,inMemoryDataStorage.getHistoryFactoryList().size());


        inMemoryDataStorage.updateCurrentFactory(createInitialFactory());


        Assert.assertEquals(2,inMemoryDataStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_addFuture()  {
        Dummy dummy = new Dummy();
        InMemoryDataStorage<Dummy,Void> fileSystemFactoryStorage = new InMemoryDataStorage<>(dummy);

        Assert.assertEquals(1,fileSystemFactoryStorage.getHistoryFactoryList().size());

        DataAndStoredMetadata<Dummy, Void> initialFactory = createInitialFactory();
        ScheduledDataMetadata<Void> scheduledDataMetadata = new ScheduledDataMetadata<>(
                initialFactory.metadata.creationTime,
                UUID.randomUUID().toString(),
                initialFactory.metadata.user,
                initialFactory.metadata.comment,
                initialFactory.metadata.baseVersionId,
                initialFactory.metadata.changeSummary,
                initialFactory.metadata.generalStorageFormat,
                initialFactory.metadata.dataStorageMetadataDictionary,
                LocalDateTime.now()
        );
        fileSystemFactoryStorage.addFutureFactory(new DataAndScheduledMetadata<>(new Dummy(),scheduledDataMetadata));


        Assert.assertEquals(1,fileSystemFactoryStorage.getFutureFactoryList().size());
    }

}