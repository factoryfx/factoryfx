package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.typescript.generator.TsGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestGenerator {

    public static void main(String[] args) {
        Path targetDirTs = Paths.get("./build/ts/example");
        if (!new File("./build").exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }
        targetDirTs.toFile().mkdirs();

        List<Class<? extends FactoryBase<?, ExampleData>>> factoryClasses = List.of(ExampleData.class, ExampleData2.class, ExampleData3.class, ExampleDataAll.class, ExampleDataIgnore.class, ExampleFactory.class);
        TsGenerator<ExampleData> tsClassCreator=new TsGenerator<>(targetDirTs, factoryClasses);
        tsClassCreator.clearTargetDir();
        tsClassCreator.generateTs();

    }

}




