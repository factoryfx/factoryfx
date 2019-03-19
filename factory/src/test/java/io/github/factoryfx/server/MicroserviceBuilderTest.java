package io.github.factoryfx.server;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MicroserviceBuilderTest {

    @TempDir
    public Path folder;

    @Test
    public void test_init_no_existing_factory()   {
        FactoryTreeBuilder< ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx-> new ExampleFactoryA());

        Assertions.assertEquals(folder.toFile().listFiles().length,0);
        builder.microservice().withFilesystemStorage(Paths.get(folder.toFile().toURI())).build().start();
        Assertions.assertEquals(folder.toFile().listFiles().length,3);
    }

    @Test
    public void test_custom_ObjectMapper() throws IOException {
        FactoryTreeBuilder< ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.stringAttribute.set("12323");
            factoryA.referenceAttribute.set(new ExampleFactoryB());
            return factoryA;
        });
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        Microservice<ExampleLiveObjectA, ExampleFactoryA, Void> microservice = builder.microservice(ObjectMapperBuilder.buildNew(yamlFactory)).withFilesystemStorage(Paths.get(folder.toFile().toURI())).build();
        microservice.start();
//        for (File file : folder.getRoot().listFiles()[0]) {
//            System.out.println(file.getAbsoluteFile());
//        }
        DataUpdate<ExampleFactoryA> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("hjhjggjhgjh");

        microservice.updateCurrentFactory(update);

        System.out.println(Files.readString(folder.toFile().listFiles()[0].toPath()));
        Assertions.assertTrue(Files.readString(folder.toFile().listFiles()[0].toPath()).contains("---"));
    }

}