package io.github.factoryfx.factory.typescript.generator.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.typescript.generator.TsGenerator;
import io.github.factoryfx.factory.typescript.generator.testserver.TestServerMain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleGenerator  {

    public static void main(String[] args) {
        Path targetDirJs = Paths.get("src/test/js/");
        Path targetDirTs = Paths.get("src/test/ts/example");
        if (!targetDirJs.toFile().exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }

        List<Class<? extends FactoryBase<?, ExampleData>>> factoryClasses = List.of(ExampleData.class, ExampleData2.class, ExampleData3.class, ExampleDataAll.class, ExampleDataIgnore.class, ExampleFactory.class);
        TsGenerator<ExampleData> tsClassCreator=new TsGenerator<>(targetDirTs, factoryClasses);
        tsClassCreator.clearTargetDir();
        tsClassCreator.generateTs();

        TsGenerator<ExampleData> jsClassCreator=new TsGenerator<>(targetDirJs, factoryClasses);
        jsClassCreator.clearTargetDir();
        jsClassCreator.generateJs();

        TestServerMain.main(null);
    }

}




