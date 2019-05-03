package io.github.factoryfx.starter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class SmokeTest {

    @Test
    public void test(@TempDir Path tempDir){
        new InitialProjectSetup(tempDir, "io.github.factoryfx").generateInitialSetup();
    }
}
