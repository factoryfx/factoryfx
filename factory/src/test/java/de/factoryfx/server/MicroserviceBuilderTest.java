package de.factoryfx.server;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
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
        Assert.assertEquals(folder.getRoot().listFiles().length,0);
        MicroserviceBuilder.buildFilesystemMicroservice(new ExampleFactoryA(),Paths.get(folder.getRoot().toURI())).start();
        Assert.assertEquals(folder.getRoot().listFiles().length,3);
    }

}