package io.github.factoryfx.factory.typescript.generator;


import com.google.common.base.StandardSystemProperty;
import com.google.common.reflect.ClassPath;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.typescript.generator.construct.*;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.*;
import io.github.factoryfx.factory.util.ClasspathBasedFactoryProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
        TsClassTemplateBased attributeAccessorClass = new TsClassTemplateBased("AttributeAccessor.ts", utilDir);
        TsClassTemplateBased attributeMetadataTsClass = new TsClassTemplateBased("AttributeMetadata.ts", utilDir);
        TsClassTemplateBased dynamicDataDictionaryTsClass = new TsClassTemplateBased("DynamicDataDictionary.ts", utilDir);
        TsClassTemplateBased attributeTypeEnumTs = new TsClassTemplateBased("AttributeType.ts", utilDir);

        try {
            for (ClassPath.ResourceInfo resourceInfo: ClassPath.from(ClasspathBasedFactoryProvider.class.getClassLoader()).getResources()) {
                String tsBasePath = "io/github/factoryfx/factory/typescript/generator/ts/";
                if (resourceInfo.getResourceName().startsWith(tsBasePath) && resourceInfo.getResourceName().endsWith(".ts")) {
                    if (resourceInfo.getResourceName().endsWith("DataCreator.ts")){
                        continue;
                    }
                    TsClassTemplateBased fileTs = new TsClassTemplateBased(utilDir.resolve(Path.of(tsBasePath).relativize(Path.of(resourceInfo.getResourceName()))).getParent(),"/"+resourceInfo.getResourceName());
                    fileTs.writeToFile();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



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
            data.internal().visitAttributesMetadata((attributeMetadata) -> {
                if (attributeMetadata.enumClass!=null){
                    enumClasses.add(attributeMetadata.enumClass);
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
            String osNameMatch = StandardSystemProperty.OS_NAME.value();
            if (osNameMatch!=null){
                osNameMatch=osNameMatch.toLowerCase();
            }
            String first="cmd";
            String second="/c";
            if(osNameMatch!=null && osNameMatch.contains("linux")) {
                first="/bin/bash";
                second="-c";
            }

            ProcessBuilder pb = new ProcessBuilder(first, second, "tsc", "--outDir", targetDir.toFile().getAbsolutePath()).directory(tsBuildDirectory.toFile().getAbsoluteFile());
            Process process;
            process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))){
                StringBuilder builder = new StringBuilder();
                String line;
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
