package de.factoryfx.server;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.factory.FactoryReferenceListTest;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MicroserviceBuilderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test_init_no_existing_factory()   {
        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx-> new ExampleFactoryA());

        Assert.assertEquals(folder.getRoot().listFiles().length,0);
        builder.microservice().withFilesystemStorage(Paths.get(folder.getRoot().toURI())).build().start();
        Assert.assertEquals(folder.getRoot().listFiles().length,3);
    }

    @Test
    public void test_custom_ObjectMapper() throws IOException {
        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.stringAttribute.set("12323");
            factoryA.referenceAttribute.set(new ExampleFactoryB());
            return factoryA;
        });
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        Microservice<Void, ExampleLiveObjectA, ExampleFactoryA, Void> microservice = builder.microservice().withJacksonObjectMapper(ObjectMapperBuilder.buildNew(yamlFactory)).withFilesystemStorage(Paths.get(folder.getRoot().toURI())).build();
        microservice.start();
//        for (File file : folder.getRoot().listFiles()[0]) {
//            System.out.println(file.getAbsoluteFile());
//        }
        DataAndStoredMetadata<ExampleFactoryA, Void> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("hjhjggjhgjh");

        microservice.updateCurrentFactory(update);

        System.out.println(Files.readString(folder.getRoot().listFiles()[0].toPath()));
        Assert.assertTrue(Files.readString(folder.getRoot().listFiles()[0].toPath()).contains("---"));
    }

}