package io.github.factoryfx.initializr.template;

import java.io.IOException;
import java.nio.file.Path;

import javax.lang.model.element.Modifier;

import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;

public class ServerBuilderTemplate {

    private final String projectName;
    private final Path targetDir;
    private final RootFactoryTemplate rootFactoryTemplate;
    private final String packageName ;
    private final ExampleResourceFactoryTemplate exampleResourceFactoryTemplate;


    public ServerBuilderTemplate(String projectName, RootFactoryTemplate rootFactoryTemplate, Path targetDir, String packageName, ExampleResourceFactoryTemplate exampleResourceFactoryTemplate) {
        this.projectName = projectName;
        this.targetDir = targetDir;
        this.rootFactoryTemplate = rootFactoryTemplate;
        this.packageName = packageName;
        this.exampleResourceFactoryTemplate = exampleResourceFactoryTemplate;
    }

    public void generateFile(){
        ParameterizedTypeName factoryTreeBuilderType = ParameterizedTypeName.get(ClassName.get(FactoryTreeBuilder.class), ClassName.get(Server.class), ClassName.bestGuess(rootFactoryTemplate.generate().name));

        MethodSpec build = MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC)
                .returns(factoryTreeBuilderType)
                .addStatement("return this.builder")
                .build();

//        AnnotationSpec annotationSpec = AnnotationSpec.builder(SuppressWarnings.class)
//                .addMember("value", "$S", "unchecked")
//                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.builder= new FactoryTreeBuilder<>($N.class)", rootFactoryTemplate.generate().name)
                .addStatement("this.builder.addBuilder((ctx)->\n"
                        + "            new $T<$N>()\n" +
                        "                    .withHost(\"localhost\").withPort(8080)\n" +
                        "                    .withResource(ctx.get($N.class)))"
                    , SimpleJettyServerBuilder.class, rootFactoryTemplate.getName(), exampleResourceFactoryTemplate.getName())
                .addStatement("this.builder.addSingleton($N.class)",exampleResourceFactoryTemplate.getName())
                .addComment("register more factories here")
//                .addAnnotation(annotationSpec)
                .build();

        TypeSpec serverBuilder = TypeSpec.classBuilder(projectName+"Builder")
                .addField(factoryTreeBuilderType, "builder", Modifier.PRIVATE, Modifier.FINAL)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor)
                .addMethod(build)
                .addJavadoc("Utility class to construct the factory tree")
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, serverBuilder)
                .build();
        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
