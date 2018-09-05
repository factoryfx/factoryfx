package de.factoryfx.factory.typescript.generator.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.typescript.generator.TsGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleGenerator extends Data {

    public static void main(String[] args) {
        Path targetDir = Paths.get("src/test/java/de/factoryfx/factory/typescript/generator/ts/example/");
        if (!targetDir.toFile().exists()){
//            System.out.println(targetDir.toFile().getAbsoluteFile());
            throw new IllegalArgumentException("set intellij working dir to $MODULE_WORKING_DIR$");
        }

        TsGenerator tsClassCreator=new TsGenerator(targetDir,List.of(ExampleData.class,ExampleData2.class));
        tsClassCreator.generate();


        ExampleData data = new ExampleData();
        data.attribute.set("123");
//        ExampleData2 exampleData2 = new ExampleData2();
//        data.ref.set(exampleData2);
//        data.refList.add(new ExampleData2());
//        data.refList.add(exampleData2);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(data));

    }

}
