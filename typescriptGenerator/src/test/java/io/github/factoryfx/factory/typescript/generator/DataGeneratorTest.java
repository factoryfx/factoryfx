package io.github.factoryfx.factory.typescript.generator;

import io.github.factoryfx.factory.typescript.generator.data.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DataGeneratorTest {


    @Test
    public void smoketest(@TempDir Path targetDir) throws IOException {
        TsGenerator<ExampleData> tsClassCreator=new TsGenerator<>(targetDir, List.of(ExampleData.class, ExampleData2.class, ExampleDataAll.class , ExampleDataIgnore.class, ExampleFactory.class));
        tsClassCreator.generateJs();

    }



}

