package io.github.factoryfx.factory.typescript.generator;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.DataDictionary;
import io.github.factoryfx.data.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.typescript.generator.construct.*;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.TsClassConstructed;
import io.github.factoryfx.factory.typescript.generator.ts.TsClassTemplateBased;
import io.github.factoryfx.factory.typescript.generator.ts.TsEnumConstructed;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TsGenerator {

    private final Path targetDir;
    private final List<Class<? extends Data>> dataClasses;
    private final BiFunction<Set<TsEnumConstructed>,Map<Class<? extends Data>, TsClassConstructed>, AttributeToTsMapperManager> attributeInfoMapperCreator;


    public TsGenerator(Path targetDir, List<Class<? extends Data>> dataClasses, BiFunction<Set<TsEnumConstructed>,Map<Class<? extends Data>, TsClassConstructed>, AttributeToTsMapperManager> attributeInfoMapperCreator) {
        this.targetDir = targetDir;
        this.dataClasses = dataClasses;
        this.attributeInfoMapperCreator = attributeInfoMapperCreator;
    }

    public TsGenerator(Path targetDir, List<Class<? extends Data>> dataClasses) {
        this(targetDir,dataClasses,(enums,dataToOverrideTs)-> new AttributeToTsMapperManager(AttributeToTsMapperManager.createAttributeInfoMap(dataToOverrideTs,enums), AttributeToTsMapperManager.createAttributeIgnoreSet()));
    }

    public void clearTargetDir(){
        if (Files.exists(targetDir)){
            try {
                final List<Path> pathsToDelete = Files.walk(targetDir).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                pathsToDelete.remove(pathsToDelete.size() - 1);
                for (Path path : pathsToDelete) {
                    Files.deleteIfExists(path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void generate(){
        targetDir.toFile().mkdirs();

        Path utilDir = targetDir.resolve("util");
        TsClassTemplateBased dataTsClass = new TsClassTemplateBased("Data.ts", utilDir);
        dataTsClass.writeToFile();

        TsClassTemplateBased attributeAccessorClass = new TsClassTemplateBased("AttributeAccessor.ts", utilDir);
        attributeAccessorClass.writeToFile();

        TsClassTemplateBased attributeIterationGroupClass = new TsClassTemplateBased("AttributeIterationGroup.ts", utilDir);
        attributeIterationGroupClass.writeToFile();



        TsClassTemplateBased attributeMetadataTsClass = new TsClassTemplateBased("AttributeMetadata.ts", utilDir);
        attributeMetadataTsClass.writeToFile();

        TsClassTemplateBased validationErrorTsClass = new TsClassTemplateBased("ValidationError.ts", utilDir);
        validationErrorTsClass.writeToFile();

        HashMap<Class<? extends Data>,TsClassConstructed> dataToGeneratedTsClass = new HashMap<>();
        HashMap<Class<? extends Data>,TsClassConstructed> dataToConfigTs = new HashMap<>();
        Path generatedPath = targetDir.resolve("generated");

        for (Class<? extends Data> dataClass : dataClasses) {
            dataToGeneratedTsClass.put(dataClass,new TsClassConstructed(dataClass.getSimpleName()+"Generated", dataClass.getPackage().getName().replace(".","/")+"/", generatedPath));
            dataToConfigTs.put(dataClass,new TsClassConstructed(dataClass.getSimpleName(),dataClass.getPackage().getName().replace(".","/")+"/", targetDir.resolve("config")));
        }


        Set<Class<? extends Enum<?>>> enumClasses= new HashSet<>();
        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToGeneratedTsClass.entrySet()) {
            Class<? extends Data> dataClass = entry.getKey();
            Data data = DataDictionary.getDataDictionary(dataClass).newInstance();
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof EnumAttribute){
                    enumClasses.add(((EnumAttribute<?>)attribute).internal_getEnumClass());
                }
            });
        }
        Set<TsEnumConstructed> enums= new HashSet<>();
        for (Class<? extends Enum<?>> enumClass : enumClasses) {
            DataEnumTs enumTs = new DataEnumTs(enumClass, generatedPath);
            TsEnumConstructed tsEnumConstructed = enumTs.construct();
            enums.add(tsEnumConstructed);
            tsEnumConstructed.writeToFile();
        }

        AttributeToTsMapperManager attributeToTsMapperManager = attributeInfoMapperCreator.apply(enums, dataToConfigTs);

        AttributeTypeEnumTs attributeTypeEnumTs = new AttributeTypeEnumTs(attributeToTsMapperManager, utilDir);
        TsEnumConstructed attributeTypeEnumTsEnum = attributeTypeEnumTs.construct();
        attributeTypeEnumTsEnum.writeToFile();

        DataCreatorTs dataCreatorGenerator = new DataCreatorTs(dataClasses, dataToConfigTs, dataTsClass, utilDir);
        TsFile dataCreatorTsClass = dataCreatorGenerator.construct();
        dataCreatorTsClass.writeToFile();

        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToGeneratedTsClass.entrySet()) {
            DataGeneratedTs dataGenerator = new DataGeneratedTs(entry.getKey(), dataToConfigTs, dataTsClass, dataCreatorTsClass, attributeMetadataTsClass, attributeAccessorClass, attributeToTsMapperManager,attributeTypeEnumTsEnum);
            dataGenerator.complete(entry.getValue()).writeToFile();
        }

        for (Map.Entry<Class<? extends Data>, TsClassConstructed> entry : dataToConfigTs.entrySet()) {
            DataConfigTs dataOverrideGenerator = new DataConfigTs(entry.getKey(), dataToGeneratedTsClass.get(entry.getKey()));
            dataOverrideGenerator.complete(entry.getValue()).writeToFile();
        }


    }
}
