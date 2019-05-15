package io.github.factoryfx.initializr.template;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class ExampleResourceFactoryTemplate {
    private final Path targetDir;
    private final String packageName;
    private final BaseFactoryTemplate baseFactoryTemplate;
    private final ExampleResourceTemplate exampleResourceTemplate;

    public ExampleResourceFactoryTemplate(Path targetDir, String packageName, BaseFactoryTemplate baseFactoryTemplate, ExampleResourceTemplate exampleResourceTemplate ) {
        this.targetDir = targetDir;
        this.packageName = packageName;
        this.baseFactoryTemplate = baseFactoryTemplate;
        this.exampleResourceTemplate = exampleResourceTemplate;
    }

    public TypeSpec generateFile(){
        MethodSpec create = MethodSpec.methodBuilder("createImpl")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.bestGuess(exampleResourceTemplate.getName()))
                .addStatement("return new $N()",exampleResourceTemplate.getName())
                .addAnnotation(Override.class)
                .build();

        TypeSpec exampleResourceFactory = TypeSpec.classBuilder(getName())
                .superclass((ParameterizedTypeName.get(ClassName.bestGuess(baseFactoryTemplate.getName()),
                        ClassName.bestGuess(exampleResourceTemplate.getName()))))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(create)
                .addJavadoc("Factory for the example resource")
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, exampleResourceFactory)
                .build();
        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  exampleResourceFactory;
    }

    public String getName() {
        return "ExampleResourceFactory";
    }
}
