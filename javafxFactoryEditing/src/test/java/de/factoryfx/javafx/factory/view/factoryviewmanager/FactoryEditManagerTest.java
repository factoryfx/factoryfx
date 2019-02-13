package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.server.Microservice;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public class FactoryEditManagerTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    @SuppressWarnings("unchecked")
    public void test_export_import() throws IOException {
        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON,ctx->{
            ExampleFactoryA initialFactory = new ExampleFactoryA();
            initialFactory.stringAttribute.set("123");
            return initialFactory;
        });

        Microservice<Void, ExampleLiveObjectA, ExampleFactoryA, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        MicroserviceRestClient<Void,ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
        Mockito.when(client.prepareNewFactory()).then(invocation -> microservice.prepareNewFactory());
        Mockito.when(client.updateCurrentFactory(Mockito.any(DataAndStoredMetadata.class),Mockito.anyString())).then(invocation -> microservice.updateCurrentFactory(invocation.getArgument(0)));

        FactoryEditManager<Void,ExampleFactoryA,Void> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
        factoryEditManager.runLaterExecuter= Runnable::run;

        factoryEditManager.load();
        Path target = tmpFolder.newFile("fghfh.json").toPath();
        factoryEditManager.saveToFile(target);
//        System.out.println(Files.readString(target));

        Assert.assertTrue(target.toFile().exists());

        factoryEditManager.loadFromFile(target);

        Assert.assertEquals("123", factoryEditManager.getLoadedFactory().get().stringAttribute.get());
    }



    private MigrationManager<ExampleFactoryA, Void> createDataMigrationManager() {
        return new MigrationManager<>(ExampleFactoryA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager(), ObjectMapperBuilder.build());
    }

    @Test
    public void test_root_merge() {
        ExampleFactoryA currentFactory = new ExampleFactoryA();
        currentFactory = currentFactory.internal().addBackReferences();

        ExampleFactoryA updateFactory = new ExampleFactoryA();
        updateFactory.referenceAttribute.set(new ExampleFactoryB());
        updateFactory = updateFactory.internal().addBackReferences();
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(currentFactory,currentFactory.utility().copy(),updateFactory);

        Assert.assertNull(currentFactory.referenceAttribute.get());
        Assert.assertNotNull(updateFactory.referenceAttribute.get());
        MergeDiffInfo<ExampleFactoryA> diff = dataMerger.mergeIntoCurrent((p) -> true);

        Assert.assertNotNull(currentFactory.referenceAttribute.get());
        Assert.assertEquals(1,diff.mergeInfos.size());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void test_import_save_differentSystem() throws IOException {

        Path target = tmpFolder.newFile("fghfh.json").toPath();
        {
            FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
            builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON,ctx->{
                ExampleFactoryA factory = new ExampleFactoryA();
                factory.referenceAttribute.set(new ExampleFactoryB());
                return factory;
            });
            Microservice<Void, ExampleLiveObjectA, ExampleFactoryA, Void> microservice = builder.microservice().withInMemoryStorage().build();

            microservice.start();


            MicroserviceRestClient<Void, ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenReturn(microservice.prepareNewFactory());

            FactoryEditManager<Void, ExampleFactoryA,Void> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
            factoryEditManager.runLaterExecuter = Runnable::run;

            factoryEditManager.load();
            factoryEditManager.saveToFile(target);
        }



        {
            FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
            builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON,ctx->{
                ExampleFactoryA factory = new ExampleFactoryA();
                return factory;
            });

            Microservice<Void, ExampleLiveObjectA, ExampleFactoryA, Void> microservice = builder.microservice().withInMemoryStorage().build();
            microservice.start();

            MicroserviceRestClient<Void, ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenAnswer(invocation -> microservice.prepareNewFactory());

            Mockito.when(client.updateCurrentFactory(Mockito.any(DataAndStoredMetadata.class),Mockito.anyString())).thenAnswer((Answer) invocation -> {
                Object[] args = invocation.getArguments();
                return microservice.updateCurrentFactory((DataAndStoredMetadata<ExampleFactoryA,Void>) args[0]);
            });

            FactoryEditManager<Void, ExampleFactoryA,Void> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
            factoryEditManager.runLaterExecuter = Runnable::run;

            factoryEditManager.load();
            Assert.assertNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());

            factoryEditManager.loadFromFile(target);
            Assert.assertNotNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());
            factoryEditManager.save("blub");

            Assert.assertNotNull(microservice.prepareNewFactory().root.referenceAttribute.get());
            Assert.assertNotNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());
        }

    }
}