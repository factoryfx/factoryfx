package io.github.factoryfx.factory.typescript.generator.ts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class TsClassTemplateBased extends TsFile {

    private final String content;

    public TsClassTemplateBased(String name, String content, Path targetPath) {
        super(name, targetPath);
        this.content = content;
    }

    /**
     *
     * @param resourcePath e.g /jg/kjg/Data.ts
     * @param targetPath targetPath
     */
    public TsClassTemplateBased(Path targetPath, String resourcePath) {
        super(Path.of(resourcePath).getFileName().toString().replace(".ts",""), targetPath);

        try (InputStream inputStream = this.getClass().getResourceAsStream(resourcePath)) {
            this.content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param resourcePathShort e.g Data.ts
     * @param targetPath targetPath
     */
    public TsClassTemplateBased(String resourcePathShort, Path targetPath) {
        super(resourcePathShort.replace(".ts",""), targetPath);

        try (InputStream inputStream = this.getClass().getResourceAsStream("/io/github/factoryfx/factory/typescript/generator/ts/"+resourcePathShort)) {
            this.content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getContent() {
        return content;
    }
}
