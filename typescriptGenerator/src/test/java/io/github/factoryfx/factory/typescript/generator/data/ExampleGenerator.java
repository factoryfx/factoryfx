package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.typescript.generator.TsGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleGenerator  {

    public static void main(String[] args) {
        Path targetDir = Paths.get("src/test/ts/example/");
        if (!Paths.get("src/test/ts").toFile().exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }

        TsGenerator<ExampleData> tsClassCreator=new TsGenerator<>(targetDir,List.of(ExampleData.class, ExampleData2.class, ExampleDataAll.class ,ExampleDataIgnore.class, ExampleFactory.class));
        tsClassCreator.clearTargetDir();
        tsClassCreator.generate();

    }

}




