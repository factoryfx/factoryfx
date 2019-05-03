package io.github.factoryfx.docu.initializr;

import io.github.factoryfx.starter.InitialProjectSetup;

import java.nio.file.Path;

class InitialProjectSetupGenerator {

    public static void main(String[] args) {
        new InitialProjectSetup(Path.of("./docu/src/main/java"), InitialProjectSetupGenerator.class.getPackageName()).generateInitialSetup();
    }

}