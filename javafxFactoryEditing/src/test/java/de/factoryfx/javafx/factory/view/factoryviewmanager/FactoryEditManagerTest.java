package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.server.Microservice;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class FactoryEditManagerTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    @SuppressWarnings("unchecked")
    public void test_export_import() throws IOException {
        DataSerialisationManager<ExampleFactoryA,Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(ExampleFactoryA.class, 1), new ArrayList<>(),1);
        MicroserviceRestClient<Void,ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
        NewDataMetadata newFactoryMetadata = new NewDataMetadata();
        newFactoryMetadata.dataModelVersion=1;
        DataAndNewMetadata<ExampleFactoryA> value = new DataAndNewMetadata<>(new ExampleFactoryA(), newFactoryMetadata);
        value.root.stringAttribute.set("123");
        Mockito.when(client.prepareNewFactory()).thenReturn(value);

        FactoryEditManager<Void,ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, serialisationManager);
        factoryEditManager.runLaterExecuter= Runnable::run;

        factoryEditManager.load();
        Path target = tmpFolder.newFile("fghfh.json").toPath();
        factoryEditManager.saveToFile(target);

        factoryEditManager.loadFromFile(target);

        Assert.assertEquals("123", factoryEditManager.getLoadedFactory().get().stringAttribute.get());




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
            DataSerialisationManager<ExampleFactoryA, Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(0), new JacksonDeSerialisation<>(ExampleFactoryA.class, 1), new ArrayList<>(), 0);

            ExampleFactoryA initialFactory = new ExampleFactoryA();
            initialFactory.referenceAttribute.set(new ExampleFactoryB());
            initialFactory = initialFactory.internal().addBackReferences();
            Microservice<Void, ExampleFactoryA, Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<ExampleFactoryA, Void>(initialFactory));
            microservice.start();


            MicroserviceRestClient<Void, ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenReturn(microservice.prepareNewFactory());

            FactoryEditManager<Void, ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, serialisationManager);
            factoryEditManager.runLaterExecuter = Runnable::run;

            factoryEditManager.load();
            factoryEditManager.saveToFile(target);
        }



        {
            DataSerialisationManager<ExampleFactoryA, Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(0), new JacksonDeSerialisation<>(ExampleFactoryA.class, 0), new ArrayList<>(), 0);

            ExampleFactoryA initialFactory = new ExampleFactoryA();
            initialFactory = initialFactory.internal().addBackReferences();
//            initialFactory.referenceAttribute.set(new ExampleFactoryB());
            Microservice<Void, ExampleFactoryA, Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), new InMemoryDataStorage<ExampleFactoryA, Void>(initialFactory));
            microservice.start();


            MicroserviceRestClient<Void, ExampleFactoryA,Void> client = Mockito.mock(MicroserviceRestClient.class);
            Mockito.when(client.prepareNewFactory()).thenAnswer(invocation -> microservice.prepareNewFactory());

            Mockito.when(client.updateCurrentFactory(Mockito.any(DataAndNewMetadata.class),Mockito.anyString())).thenAnswer((Answer) invocation -> {
                Object[] args = invocation.getArguments();
                return microservice.updateCurrentFactory((DataAndNewMetadata<ExampleFactoryA>) args[0],"","",(p)->true);
            });

            FactoryEditManager<Void, ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, serialisationManager);
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