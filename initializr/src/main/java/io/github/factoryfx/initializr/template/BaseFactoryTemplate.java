package io.github.factoryfx.initializr.template;

import com.squareup.javapoet.*;
import io.github.factoryfx.factory.SimpleFactoryBase;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;


public class BaseFactoryTemplate {
    private final String projectName;
    private final Path targetDir;
    private final RootFactoryTemplate rootFactoryTemplate;
    private final String packageName ;


    public BaseFactoryTemplate(String projectName, RootFactoryTemplate rootFactoryTemplate, Path targetDir, String packageName) {
        this.projectName = projectName;
        this.targetDir = targetDir;
        this.rootFactoryTemplate = rootFactoryTemplate;
        this.packageName = packageName;
    }

    public void generateFile(){
        TypeVariableName typeVariableName = TypeVariableName.get("L");

        TypeSpec baseFactory = TypeSpec.classBuilder(getName())
                .superclass((ParameterizedTypeName.get(ClassName.get(SimpleFactoryBase.class),
                        typeVariableName,ClassName.bestGuess(rootFactoryTemplate.generate().name))))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(typeVariableName)
                .addJavadoc("Base factory for all factories in the project")
                .build();



        JavaFile javaFile = JavaFile.builder(packageName, baseFactory)
                .build();


        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return projectName+"BaseFactory";
    }
}
