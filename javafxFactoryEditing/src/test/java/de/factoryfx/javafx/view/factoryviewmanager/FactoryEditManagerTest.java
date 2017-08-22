package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.datastorage.*;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.junit.Assert.*;


public class FactoryEditManagerTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    @SuppressWarnings("unchecked")
    public void test_export_import() throws IOException {
        FactorySerialisationManager<ExampleFactoryA> serialisationManager = new FactorySerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(ExampleFactoryA.class, 1), new ArrayList<>(),1);
        ApplicationServerRestClient<Void,ExampleFactoryA> client = Mockito.mock(ApplicationServerRestClient.class);
        NewFactoryMetadata newFactoryMetadata = new NewFactoryMetadata();
        newFactoryMetadata.dataModelVersion=1;
        FactoryAndNewMetadata<ExampleFactoryA> value = new FactoryAndNewMetadata<>(new ExampleFactoryA(), newFactoryMetadata);
        value.root.stringAttribute.set("123");
        Mockito.when(client.prepareNewFactory()).thenReturn(value);

        FactoryEditManager<Void,ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, serialisationManager);
        factoryEditManager.runLaterExecuter= Runnable::run;

        factoryEditManager.load();
        Path target = tmpFolder.newFile("fghfh.json").toPath();
        factoryEditManager.saveToFile(target);

        factoryEditManager.loadFromFile(target);

        Assert.assertEquals("123", factoryEditManager.getLoadedFactory().get().stringAttribute.get());



    };

}