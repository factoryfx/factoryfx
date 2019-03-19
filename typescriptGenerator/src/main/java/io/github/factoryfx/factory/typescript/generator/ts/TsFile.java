package io.github.factoryfx.factory.typescript.generator.ts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class TsFile {

    private final String name;
    private final String subPath;
    private final Path targetBasePath;

    protected TsFile(String name, String subPath, Path targetBasePath) {
        this.name = name;
        this.subPath = subPath;
        this.targetBasePath = targetBasePath;
    }

    protected TsFile(String name, Path targetBasePath) {
        this.name = name;
        this.subPath = "";
        this.targetBasePath = targetBasePath;
    }

    protected abstract String getContent();

    public void writeToFile(){
        try {
            Path fileName = getFileName();
            Path parent = fileName.getParent();
            if (parent!=null){
                Files.createDirectories(parent);
            }
            Files.writeString(fileName, "//generated code don't edit manually\n"+getContent(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToFileOnce(){
        try {
            Path fileName = getFileName();
            Path parent = fileName.getParent();
            if (parent!=null){
                Files.createDirectories(parent);
            }
            if (!Files.exists(fileName)){
                Files.writeString(fileName, "//generated once\n"+getContent(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    private Path getFileName() {
        return targetBasePath.resolve(subPath+name+".ts");
    }

    public Path getRelativePathToFileName(TsFile tsFile){
        Path parent = tsFile.getFileName().getParent();
        if (parent!=null){
            return parent.relativize(getFileName());
        }
        return tsFile.getFileName().relativize(getFileName());
    }
}
