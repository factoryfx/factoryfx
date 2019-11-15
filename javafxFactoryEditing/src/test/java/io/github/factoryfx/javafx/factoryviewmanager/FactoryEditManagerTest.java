package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.factory.storage.DataUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;


public class FactoryEditManagerTest {

    @TempDir
    public Path tmpFolder;

    @Test
    @SuppressWarnings("unchecked")
    public void test_export_import() throws IOException {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx->{
            ExampleFactoryA initialFactory = new ExampleFactoryA();
            initialFactory.stringAttribute.set("123");
            return initialFactory;
        });

        Microservice<ExampleLiveObjectA, ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        MicroserviceRestClient<ExampleFactoryA> client = Mockito.mock(MicroserviceRestClient.class);
        Mockito.when(client.prepareNewFactory()).then(invocation -> microservice.prepareNewFactory());
        Mockito.when(client.updateCurrentFactory(Mockito.any(DataUpdate.class),Mockito.anyString())).then(invocation -> microservice.updateCurrentFactory(invocation.getArgument(0)));

        FactoryEditManager<ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
        factoryEditManager.runLaterExecuter= Runnable::run;

        factoryEditManager.load();

        Path target = Files.createFile(tmpFolder.resolve("fghfh.json"));

        factoryEditManager.saveToFile(target);
//        System.out.println(Files.readString(target));

        Assertions.assertTrue(target.toFile().exists());

        factoryEditManager.loadFromFile(target);

        Assertions.assertEquals("123", factoryEditManager.getLoadedFactory().get().stringAttribute.get());
    }



    private MigrationManager<ExampleFactoryA> createDataMigrationManager() {
        return new MigrationManager<>(ExampleFactoryA.class, ObjectMapperBuilder.build(), (root, d) -> { });
    }

    @Test
    public void test_root_merge() {
        ExampleFactoryA currentFactory = new ExampleFactoryA();
        currentFactory = currentFactory.internal().finalise();

        ExampleFactoryA updateFactory = new ExampleFactoryA();
        updateFactory.referenceAttribute.set(new ExampleFactoryB());
        updateFactory = updateFactory.internal().finalise();
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(currentFactory,currentFactory.utility().copy(),updateFactory);

        Assertions.assertNull(currentFactory.referenceAttribute.get());
        Assertions.assertNotNull(updateFactory.referenceAttribute.get());
        MergeDiffInfo<ExampleFactoryA> diff = dataMerger.mergeIntoCurrent((p) -> true);

        Assertions.assertNotNull(currentFactory.referenceAttribute.get());
        Assertions.assertEquals(1,diff.mergeInfos.size());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void test_import_save_differentSystem() throws IOException {

        Path target = Files.createFile(tmpFolder.resolve("fghfh.json"));
        {
            FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class,ctx->{
                ExampleFactoryA factory = new ExampleFactoryA();
                factory.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
                return factory;
            });
            builder.addSingleton(ExampleFactoryB.class, ctx-> {
                return new ExampleFactoryB();
            });
            Microservice<ExampleLiveObjectA, ExampleFactoryA> microservice = builder.microservice().build();

            microservice.start();


            MicroserviceRestClient<ExampleFactoryA> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenReturn(microservice.prepareNewFactory());

            FactoryEditManager<ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
            factoryEditManager.runLaterExecuter = Runnable::run;

            factoryEditManager.load();
            factoryEditManager.saveToFile(target);
        }



        {
            FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class,ctx->{
                ExampleFactoryA factory = new ExampleFactoryA();
                return factory;
            });

            Microservice<ExampleLiveObjectA, ExampleFactoryA> microservice = builder.microservice().build();
            microservice.start();

            MicroserviceRestClient<ExampleFactoryA> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenAnswer(invocation -> microservice.prepareNewFactory());

            Mockito.when(client.updateCurrentFactory(Mockito.any(DataUpdate.class),Mockito.anyString())).thenAnswer((Answer) invocation -> {
                Object[] args = invocation.getArguments();
                return microservice.updateCurrentFactory((DataUpdate<ExampleFactoryA>) args[0]);
            });

            FactoryEditManager<ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, createDataMigrationManager());
            factoryEditManager.runLaterExecuter = Runnable::run;

            factoryEditManager.load();
            Assertions.assertNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());

            factoryEditManager.loadFromFile(target);
            Assertions.assertNotNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());
            factoryEditManager.save("blub");

            Assertions.assertNotNull(microservice.prepareNewFactory().root.referenceAttribute.get());
            Assertions.assertNotNull(factoryEditManager.getLoadedFactory().get().referenceAttribute.get());
        }

    }
}