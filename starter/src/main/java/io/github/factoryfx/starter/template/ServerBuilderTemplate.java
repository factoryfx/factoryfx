package io.github.factoryfx.starter.template;

import com.squareup.javapoet.*;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

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
        ParameterizedTypeName factoryTreeBuilderType = ParameterizedTypeName.get(ClassName.get(FactoryTreeBuilder.class), ClassName.get(Server.class), ClassName.bestGuess(rootFactoryTemplate.generate().name), ClassName.get(Void.class));

        MethodSpec build = MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC)
                .returns(factoryTreeBuilderType)
                .addStatement("return this.builder")
                .build();

        AnnotationSpec annotationSpec = AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "$S", "unchecked")
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.builder= new FactoryTreeBuilder<>($N.class)", rootFactoryTemplate.generate().name)
                .addStatement("this.builder.addFactory($T.class,$T.SINGLETON,(ctx)->{\n"
                        + "            return new $T<>(new JettyServerFactory<$N>())\n" +
                        "                    .withHost(\"localhost\").withPort(8080)\n" +
                        "                    .withResource(ctx.get($N.class)).build();\n"
                        + "})",JettyServerFactory.class, Scope.class, JettyServerBuilder.class, rootFactoryTemplate.generate().name, exampleResourceFactoryTemplate.getName())
                .addStatement("this.builder.addFactory($N.class,$T.SINGLETON)",exampleResourceFactoryTemplate.getName(),Scope.class)
                .addComment("register more factories here")
                .addAnnotation(annotationSpec)
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
