package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OracledbDataStorageTest extends DatabaseTest{

    private DataAndStoredMetadata<ExampleFactoryA,Void> createInitialExampleFactoryA() {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        GeneralStorageMetadata generalStorageMetadata = GeneralStorageMetadataBuilder.build();
        DataStorageMetadataDictionary dataStorageMetadataDictionary = new DataStorageMetadataDictionary(Set.of(exampleFactoryA.getClass()));
        DataAndStoredMetadata<ExampleFactoryA,Void> initialFactoryAndStorageMetadata = new DataAndStoredMetadata<>(exampleFactoryA,
                new StoredDataMetadata<>(LocalDateTime.now(),
                        UUID.randomUUID().toString(),
                        "System",
                        "initial factory",
                        UUID.randomUUID().toString(),
                        null, generalStorageMetadata,
                        dataStorageMetadataDictionary
                )
        );
        return initialFactoryAndStorageMetadata;
    }

    @Test
    public void test_init_no_existing_factory() {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), createMigrationManager());
        oracledbFactoryStorage.getCurrentFactory();

        Assert.assertEquals(1,oracledbFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_init_existing_factory() {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), createMigrationManager());
        String id=oracledbFactoryStorage.getCurrentFactory().id;

        OracledbDataStorage<ExampleFactoryA,Void> restored = new OracledbDataStorage<>(connectionSupplier,null, createMigrationManager());
        Assert.assertEquals(id,restored.getCurrentFactory().id);
    }

    @Test
    public void test_update()  {
        OracledbDataStorage<ExampleFactoryA,Void> oracledbFactoryStorage = new OracledbDataStorage<>(connectionSupplier, createInitialExampleFactoryA(), createMigrationManager());
        DataAndStoredMetadata<ExampleFactoryA, Void> currentFactory = createInitialExampleFactoryA();
        String id=  oracledbFactoryStorage.getCurrentFactory().id;

        StoredDataMetadata<Void> updateMetadata = new StoredDataMetadata<>(LocalDateTime.now(),
                UUID.randomUUID().toString(),
                "user",
                "update",
                currentFactory.metadata.id,
                null,
                currentFactory.metadata.generalStorageMetadata,
                currentFactory.metadata.dataStorageMetadataDictionary
        );
        oracledbFactoryStorage.updateCurrentFactory(new DataAndStoredMetadata<>(new ExampleFactoryA(),updateMetadata));
        Assert.assertNotEquals(id,oracledbFactoryStorage.getCurrentFactory() .id);
        Assert.assertEquals(2,oracledbFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        oracledbFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }

}