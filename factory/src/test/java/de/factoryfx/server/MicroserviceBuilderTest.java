package de.factoryfx.server;

import de.factoryfx.factory.FactoryReferenceListTest;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.MicroserviceBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Paths;

public class MicroserviceBuilderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test_init_no_existing_factory()   {
        FactoryTreeBuilder<Void, ExampleLiveObjectA, ExampleFactoryA, Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, ctx-> new ExampleFactoryA());

        Assert.assertEquals(folder.getRoot().listFiles().length,0);
        builder.microservice().withFilesystemStorage(Paths.get(folder.getRoot().toURI())).build().start();;
        Assert.assertEquals(folder.getRoot().listFiles().length,3);
    }

}