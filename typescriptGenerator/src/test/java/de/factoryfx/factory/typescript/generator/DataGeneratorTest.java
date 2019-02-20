package de.factoryfx.factory.typescript.generator;

import de.factoryfx.factory.typescript.generator.data.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DataGeneratorTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    @Test
    public void smoketest() throws IOException {
        File tmpFolder= folder.newFolder("generatortest");

        Path targetDir = tmpFolder.toPath();
        if (!targetDir.toFile().exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }

        TsGenerator tsClassCreator=new TsGenerator(targetDir, List.of(ExampleData.class, ExampleData2.class, ExampleDataAll.class , ExampleDataIgnore.class, ExampleFactory.class));
        tsClassCreator.generate();

    }



}

