package de.factoryfx.factory.typescript.generator;

import de.factoryfx.data.Data;
import de.factoryfx.factory.typescript.generator.construct.DataCreatorTs;
import de.factoryfx.factory.typescript.generator.construct.DataGeneratedTs;
import de.factoryfx.factory.typescript.generator.construct.DataConfigTs;
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
        TsClassFile dataTsClass = new TsClassTemplateBased("Data.ts", targetDir.resolve("data"));
        dataTsClass.writeToFile();

        TsClassFile attributeAccessorClass = new TsClassTemplateBased("AttributeAccessor.ts", targetDir.resolve("data"));
        attributeAccessorClass.writeToFile();

        TsClassFile attributeIterationGroupClass = new TsClassTemplateBased("AttributeIterationGroup.ts", targetDir.resolve("data"));
        attributeIterationGroupClass.writeToFile();



        TsClassFile attributeMetadataTsClass = new TsClassTemplateBased("AttributeMetadata.ts", targetDir.resolve("data"));
        attributeMetadataTsClass.writeToFile();

        TsClassFile validationErrorTsClass = new TsClassTemplateBased("ValidationError.ts", targetDir.resolve("data"));
        validationErrorTsClass.writeToFile();

        HashMap<Class<? extends Data>,TsClassConstructed> dataToGeneratedTsClass = new HashMap<>();
        HashMap<Class<? extends Data>,TsClassConstructed> dataToConfigTs = new HashMap<>();
        for (Class<? extends Data> dataClass : dataClasses) {
            dataToGeneratedTsClass.put(dataClass,new TsClassConstructed(dataClass.getSimpleName()+"Generated", targetDir.resolve("generated")));
            dataToConfigTs.put(dataClass,new TsClassConstructed(dataClass.getSimpleName(), targetDir.resolve("config")));
        }



        DataCreatorTs dataCreatorGenerator = new DataCreatorTs(dataClasses,dataToConfigTs,dataTsClass,targetDir.resolve("data"));
        TsClassFile dataCreatorTsClass = dataCreatorGenerator.construct();
        dataCreatorTsClass.writeToFile();

        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToGeneratedTsClass.entrySet()) {
            DataGeneratedTs dataGenerator = new DataGeneratedTs(entry.getKey(),dataToConfigTs,dataTsClass,dataCreatorTsClass, attributeMetadataTsClass,attributeAccessorClass);
            dataGenerator.complete(entry.getValue()).writeToFile();
        }

        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToConfigTs.entrySet()) {
            DataConfigTs dataOverrideGenerator = new DataConfigTs(entry.getKey(),dataToGeneratedTsClass.get(entry.getKey()));
            dataOverrideGenerator.complete(entry.getValue()).writeToFile();
        }

    }
}
