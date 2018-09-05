package de.factoryfx.factory.typescript.generator.ts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public abstract class TsClassFile {

    private final String name;
    private final Path targetPath;


    public TsClassFile(String name, Path targetPath) {
        this.name = name;
        this.targetPath = targetPath;
    }

    public String getName() {
        return name;
    }

    public abstract String generateTsFile();

    public void writeToFile(){
        try {
            Path fileName = getFileName();
            Files.createDirectories(fileName.getParent());
            Files.write(fileName, ("//generated code don't edit manually\n"+generateTsFile()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToFileOnce(){
        try {
            Path fileName = getFileName();
            Files.createDirectories(fileName.getParent());
            Files.write(fileName, ("//generated code don't edit manually\n"+generateTsFile()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getFileName() {
        return targetPath.resolve(getName()+".ts");
    }

    public Path getRelativePathToFileName(TsClassFile tsClass){
        return tsClass.getFileName().getParent().relativize(getFileName());
    }
}
