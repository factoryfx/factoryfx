package de.factoryfx.factory.typescript.generator.ts;

import de.factoryfx.factory.typescript.generator.ts.TsMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class TsClass {

    private final String name;

    public TsClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String generateTsFile();

    public void generateTsFileToFile(Path targetDir){
        try {
            Files.write(targetDir.resolve(getName()+".ts"), ("//generated code don't edit manually\n"+generateTsFile()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
