package de.factoryfx.factory.typescript.generator.ts;

import de.factoryfx.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TsClassTemplateBased extends TsClass {

    private final String content;

    public TsClassTemplateBased(String name, String content) {
        super(name);
        this.content = content;
    }

    /**
     *
     * @param resourcePathShort e.g Data.ts
     */
    public TsClassTemplateBased(String resourcePathShort) {
        super(resourcePathShort.replace(".ts",""));

        try (InputStream inputStream = this.getClass().getResourceAsStream("/de/factoryfx/factory/typescript/generator/ts/"+resourcePathShort)) {
            this.content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateTsFile() {
        return content;
    }
}