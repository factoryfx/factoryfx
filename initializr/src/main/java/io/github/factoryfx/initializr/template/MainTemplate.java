package io.github.factoryfx.initializr.template;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class MainTemplate {
    private final String projectName;
    private final Path targetDir;
    private final RootFactoryTemplate rootFactoryTemplate;
    private final String packageName ;


    public MainTemplate(String projectName, RootFactoryTemplate rootFactoryTemplate, Path targetDir, String packageName) {
        this.projectName = projectName;
        this.targetDir = targetDir;
        this.rootFactoryTemplate = rootFactoryTemplate;
        this.packageName = packageName;
    }

    public void generateFile(){
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("new ServerBuilder().builder().microservice().build().start()")
                .build();

        TypeSpec mainTypeSpec = TypeSpec.classBuilder("ServerMain")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .addJavadoc("Application start")
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, mainTypeSpec)
                .build();

        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
