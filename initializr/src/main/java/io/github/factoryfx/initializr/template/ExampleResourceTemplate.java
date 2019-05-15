package io.github.factoryfx.initializr.template;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.ws.rs.GET;
import java.io.IOException;
import java.nio.file.Path;

public class ExampleResourceTemplate {
    private final Path targetDir;
    private final String packageName;

    public ExampleResourceTemplate(Path targetDir, String packageName ) {
        this.targetDir = targetDir;
        this.packageName = packageName;
    }

    public TypeSpec generateFile(){
        String name = "ExampleResource";

        AnnotationSpec annotationSpec = AnnotationSpec.builder(javax.ws.rs.Path.class)
                .addMember("value", "$S", "/")
                .build();

        MethodSpec create = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(String.class))
                .addStatement("return \"Hello World\"")
                .addAnnotation(GET.class)
                .build();


        TypeSpec exampleResource = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(create)
                .addAnnotation(annotationSpec)
                .addJavadoc("Example jersey REST resource")
                .build();


        JavaFile javaFile = JavaFile.builder(packageName, exampleResource)
                .build();
        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  exampleResource;
    }


    public String getName() {
        return "ExampleResource";
    }
}
