package io.github.factoryfx.starter;

import io.github.factoryfx.starter.template.*;

import java.nio.file.Path;

public class InitialProjectSetup {

    private final Path sourceDir;
    private final String packageName;


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

        new FactoryAttributeTemplate(rootFactoryTemplate,sourceDir,packageName).generateFile();
        new FactoryListAttributeTemplate(rootFactoryTemplate,sourceDir,packageName).generateFile();
        new ServerBuilderTemplate("Server",rootFactoryTemplate,sourceDir,packageName,exampleResourceFactoryTemplate).generateFile();
        new MainTemplate("Server",rootFactoryTemplate,sourceDir,packageName).generateFile();
    }

}
