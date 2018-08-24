package de.factoryfx.factory.typescript.generator;

import de.factoryfx.data.Data;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TsGenerator {

    private final Path targetDir;
    private final List<Class<? extends Data>> dataClasses;

    public TsGenerator(Path targetDir, List<Class<? extends Data>> dataClasses) {
        this.targetDir = targetDir;
        this.dataClasses = dataClasses;
    }


    public void generate(){
        TsClass dataTsClass = new TsClassTemplateBased("Data.ts");
        dataTsClass.generateTsFileToFile(targetDir);

        HashMap<Class<? extends Data>,TsClassConstructed> dataToTs = new HashMap<>();
        for (Class<? extends Data> dataClass : dataClasses) {
            dataToTs.put(dataClass,new TsClassConstructed(dataClass.getSimpleName()));
        }
        DataCreatorGenerator dataCreatorGenerator = new DataCreatorGenerator(dataClasses,dataToTs,dataTsClass);
        TsClass dataCreatorTsClass = dataCreatorGenerator.construct();
        dataCreatorTsClass.generateTsFileToFile(targetDir);

        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToTs.entrySet()) {
            DataGenerator dataGenerator = new DataGenerator(entry.getKey(),dataToTs,dataTsClass,dataCreatorTsClass);
            dataGenerator.complete(entry.getValue()).generateTsFileToFile(targetDir);
        }






//
//        TsClassCreator tsClassCreator=new TsClassCreator(new DataCreatorGenerator(dataClasses));
//        tsClassCreator.create(ExampleData.class);
//
//
//        tsClassCreator.getAllTsClasses().forEach(tsClass -> tsClass.generateTsFileToFile(targetDir));
//        return constructed;
    }
}
