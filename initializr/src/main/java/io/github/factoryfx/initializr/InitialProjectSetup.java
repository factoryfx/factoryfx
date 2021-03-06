package io.github.factoryfx.initializr;

import io.github.factoryfx.initializr.template.*;

import java.nio.file.Path;

/**
 * generates java files with the initial project setup for a factoryfx application
 */
public class InitialProjectSetup {

    private final Path sourceDir;
    private final String packageName;


    /**
     *
     * @param sourceDir path to project java source folder. e.g.: ./project/src/main/java
     * @param packageName project base package name
     */
    public InitialProjectSetup(Path sourceDir, String packageName) {
        this.sourceDir = sourceDir;
        this.packageName = packageName;
    }

    public void generateInitialSetup(){
        RootFactoryTemplate rootFactoryTemplate = new RootFactoryTemplate("Server",sourceDir,packageName);
        rootFactoryTemplate.generate();
        BaseFactoryTemplate baseFactoryTemplate = new BaseFactoryTemplate("Server", rootFactoryTemplate, sourceDir, packageName);
        baseFactoryTemplate.generateFile();

        ExampleResourceTemplate exampleResourceTemplate = new ExampleResourceTemplate(sourceDir, packageName);
        exampleResourceTemplate.generateFile();
        ExampleResourceFactoryTemplate exampleResourceFactoryTemplate = new ExampleResourceFactoryTemplate(sourceDir, packageName, baseFactoryTemplate, exampleResourceTemplate);
        exampleResourceFactoryTemplate.generateFile();

        new ServerBuilderTemplate("Server",rootFactoryTemplate,sourceDir,packageName,exampleResourceFactoryTemplate).generateFile();
        new MainTemplate("Server",rootFactoryTemplate,sourceDir,packageName).generateFile();
    }

}
