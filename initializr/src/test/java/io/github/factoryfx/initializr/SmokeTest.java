package io.github.factoryfx.initializr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class SmokeTest {

    @Test
    public void test(@TempDir Path tempDir){
        new InitialProjectSetup(tempDir, "io.github.factoryfx").generateInitialSetup();
    }
}
