package io.github.factoryfx.starter;

import com.squareup.javapoet.*;
import io.github.factoryfx.factory.FactoryBase;

import javax.lang.model.element.Modifier;
import java.io.IOException;


public class ProjectFactoryBaseTemplate {
    private final String projectName;


    public ProjectFactoryBaseTemplate(String projectName) {
        this.projectName = projectName;
    }

    public static void main(String[] args) {
        new ProjectFactoryBaseTemplate("Test123").generateFile();
    }


    public void generateFile(){
//        TypeSpec factoryBase = TypeSpec.classBuilder("FactoryBase")
//                .addModifiers(Modifier.PUBLIC)
//                .build();


        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder(projectName+"BaseFactory")
                .superclass((ParameterizedTypeName.get(ClassName.get(FactoryBase.class),
                        ClassName.bestGuess(new ProjectRootFactoryTemplate(projectName).generate().name))))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();



        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
