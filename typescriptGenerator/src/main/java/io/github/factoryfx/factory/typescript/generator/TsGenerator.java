package io.github.factoryfx.factory.typescript.generator;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.typescript.generator.construct.*;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TsGenerator<R extends FactoryBase<?,R>> {

    private final Path targetDir;
    private final Collection<Class<? extends FactoryBase<?,R>>> dataClasses;
    private final AttributeToTsMapperManager.AttributeToTsMapperManagerCreator<R> attributeInfoMapperCreator;


    public TsGenerator(Path targetDir, Collection<Class<? extends FactoryBase<?,R>>> dataClasses, AttributeToTsMapperManager.AttributeToTsMapperManagerCreator<R> attributeInfoMapperCreator) {
        this.targetDir = targetDir;
        this.dataClasses = dataClasses;
        this.attributeInfoMapperCreator = attributeInfoMapperCreator;
    }

    public TsGenerator(Path targetDir, Collection<Class<? extends FactoryBase<?,R>>> dataClasses) {
        this(targetDir, dataClasses, new AttributeToTsMapperManager.AttributeToTsMapperManagerCreator<>());
    }

    public void clearTargetDir(){
        if (Files.exists(targetDir)){
            try {
                final List<Path> pathsToDelete = Files.walk(targetDir).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                pathsToDelete.remove(pathsToDelete.size() - 1);
                for (Path path : pathsToDelete) {
                    if (!path.toFile().getAbsolutePath().endsWith("index.html")) {
                        Files.deleteIfExists(path);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void generateTs(){
        generateTs(targetDir);
    }

    @SuppressWarnings("unchecked")
    private void generateTs(Path targetDir){
        Path utilDir = targetDir.resolve("util");
        TsClassTemplateBased dataTsClass = new TsClassTemplateBased("Data.ts", utilDir);
        dataTsClass.writeToFile();

        TsClassTemplateBased attributeAccessorClass = new TsClassTemplateBased("AttributeAccessor.ts", utilDir);
        attributeAccessorClass.writeToFile();

        TsClassTemplateBased attributeIterationGroupClass = new TsClassTemplateBased("AttributeIterationGroup.ts", utilDir);
        attributeIterationGroupClass.writeToFile();

        TsClassTemplateBased factoryEditorClass = new TsClassTemplateBased("FactoryEditor.ts", utilDir);
        factoryEditorClass.writeToFile();


        TsClassTemplateBased attributeMetadataTsClass = new TsClassTemplateBased("AttributeMetadata.ts", utilDir);
        attributeMetadataTsClass.writeToFile();

        TsClassTemplateBased dynamicDataDictionaryTsClass = new TsClassTemplateBased("DynamicDataDictionary.ts", utilDir);
        dynamicDataDictionaryTsClass.writeToFile();

        for (String file : List.of("ValidationError.ts", "AttributeEditor.ts", "AttributeEditorCreator.ts",
                "AttributeEditorStringAttribute.ts", "AttributeEditorFallback.ts", "AttributeEditorFactoryAttribute.ts","AttributeEditorFactoryListAttribute.ts",
                "AttributeEditorIntegerAttribute.ts","WaitAnimation.ts",
                "AttributeEditorEnumAttribute.ts", "AttributeEditorEnumListAttribute.ts", "AttributeEditorLongAttribute.ts",
                "AttributeEditorLocalDateAttribute.ts", "AttributeEditorBooleanAttribute.ts", "AttributeEditorDoubleAttribute.ts", "AttributeEditorFileContentAttribute.ts",
                "AttributeEditorStringListAttribute.ts", "DomUtility.ts", "GuiConfiguration.ts", "NavItem.ts", "Navbar.ts", "HttpUtility.ts",
                "View.ts" , "Widget.ts", "FactoryUpdateResult.ts", "SaveWidget.ts", "AttributeEditorFactoryViewAttribute.ts",
                "AttributeEditorFactoryViewListAttribute.ts", "AttributeMetadataAndAttributeName.ts", "AttributeEditorByteAttribute.ts", "AttributeEditorFloatAttribute.ts")) {
            TsClassTemplateBased fileTs = new TsClassTemplateBased(file, utilDir);
            fileTs.writeToFile();
        }


        TsClassTemplateBased dynamicDataTsClass = new TsClassTemplateBased("DynamicData.ts", utilDir);
        dynamicDataTsClass.writeToFile();



        HashMap<Class<? extends FactoryBase<?,R>>,TsClassConstructed> dataToGeneratedTsClass = new HashMap<>();
        HashMap<Class<? extends FactoryBase<?,R>>,TsClassConstructed> dataToConfigTs = new HashMap<>();
        Path generatedPath = targetDir.resolve("generated");

        for (Class<? extends FactoryBase<?,R>> dataClass : dataClasses) {
            dataToGeneratedTsClass.put(dataClass,new TsClassConstructed(dataClass.getSimpleName()+"Generated", dataClass.getPackage().getName().replace(".","/")+"/", generatedPath));
            dataToConfigTs.put(dataClass,new TsClassConstructed(dataClass.getSimpleName(),dataClass.getPackage().getName().replace(".","/")+"/", targetDir.resolve("config")));
        }

        Set<Class<? extends Enum<?>>> enumClasses= new HashSet<>();
        for (Map.Entry<Class<? extends FactoryBase<?,R>>, TsClassConstructed> entry : dataToGeneratedTsClass.entrySet()) {
            Class<? extends FactoryBase<?,R>> dataClass = entry.getKey();
            FactoryBase<?,R> data = FactoryMetadataManager.getMetadataUnsafe(dataClass).newInstance();
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof EnumAttribute){
                    enumClasses.add(((EnumAttribute<?>)attribute).internal_getEnumClass());
                }
                if (attribute instanceof EnumListAttribute){
                    enumClasses.add(((EnumListAttribute<?>)attribute).internal_getEnumClass());
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

        AttributeToTsMapperManager attributeToTsMapperManager = attributeInfoMapperCreator.create(dataToConfigTs,enums,dataTsClass);

        TsClassTemplateBased attributeTypeEnumTs = new TsClassTemplateBased("AttributeType.ts", utilDir);
        attributeTypeEnumTs.writeToFile();

        DataCreatorTs<R> dataCreatorGenerator = new DataCreatorTs<>(dataClasses, dataToConfigTs, dataTsClass, utilDir);
        TsFile dataCreatorTsClass = dataCreatorGenerator.construct();
        dataCreatorTsClass.writeToFile();

        for (Map.Entry<Class<? extends FactoryBase<?,R>>, TsClassConstructed> entry : dataToGeneratedTsClass.entrySet()) {
            DataGeneratedTs dataGenerator = new DataGeneratedTs(entry.getKey(), dataToConfigTs, dataTsClass,dynamicDataDictionaryTsClass, dataCreatorTsClass, attributeMetadataTsClass, attributeAccessorClass, attributeToTsMapperManager,attributeTypeEnumTs);
            dataGenerator.complete(entry.getValue()).writeToFile();
        }

        for (Map.Entry<Class<? extends FactoryBase<?,R>>, TsClassConstructed> entry : dataToConfigTs.entrySet()) {
            DataConfigTs dataOverrideGenerator = new DataConfigTs(entry.getKey(), dataToGeneratedTsClass.get(entry.getKey()));
            dataOverrideGenerator.complete(entry.getValue()).writeToFile();
        }
    }

    public void generateJs(){
        targetDir.toFile().mkdirs();
        Path tsBuildDirectory;
//        Path tsBuildDirectory = Path.of("./build/ts");
//        tsBuildDirectory.toFile().mkdirs();
        try {
            tsBuildDirectory = Files.createTempDirectory("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        generateTs(tsBuildDirectory);


        writeResourceFile("tsconfig.json",tsBuildDirectory.toFile().getAbsolutePath());

//        try {
//            Process processNpm = new ProcessBuilder("cmd", "/c", "npm", "install").directory(tempDirectory.toFile().getAbsoluteFile()).inheritIO().start();
//            processNpm.waitFor();
//            Process processTsc = new ProcessBuilder("cmd", "/c", "npx", "tsc", "--outDir", targetDir.toFile().getAbsolutePath()).directory(tempDirectory.toFile().getAbsoluteFile()).inheritIO().start();
//            processTsc.waitFor();
//            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "tsc", "--outDir", targetDir.toFile().getAbsolutePath()).directory(tempDirectory.toFile().getAbsoluteFile()).inheritIO();
//            processBuilder.redirectErrorStream(true);
//            Process processTsc = processBuilder.start();
//            processBuilder.redirectOutput(null)
//
//
//
//
//
//            Reader rdr = new InputStreamReader(processTsc.getInputStream());
//            StringBuilder sb = new StringBuilder();
//            for(int i; (i = rdr.read()) !=-1;) {
//                sb.append((char)i);
//            }
//            String var = sb.toString();
//            System.out.println("qqqqq"+var);
//
//            processTsc.waitFor();


        compileTsToJS(tsBuildDirectory);
    }

    private void compileTsToJS(Path tsBuildDirectory) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "tsc", "--outDir", targetDir.toFile().getAbsolutePath()).directory(tsBuildDirectory.toFile().getAbsoluteFile());
            Process process;
            process = pb.start();
//            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringJoiner sj = new StringJoiner("/n");
//            reader.lines().iterator().forEachRemaining(sj::add);
//            if (!sj.toString().isEmpty()){
//                throw new IllegalStateException("\n"+sj.toString());
//            }

            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (!builder.toString().isEmpty()) {
                    throw new IllegalStateException("\n" + builder.toString());
                }
            }





            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void writeResourceFile(String resourcePathShort, String targetDir){
        try (InputStream inputStream = this.getClass().getResourceAsStream("/io/github/factoryfx/factory/typescript/generator/ts/"+resourcePathShort)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(targetDir + "/" + resourcePathShort))){
                inputStream.transferTo(fileOutputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
