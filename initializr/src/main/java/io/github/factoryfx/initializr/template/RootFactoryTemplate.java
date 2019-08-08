package io.github.factoryfx.initializr.template;

import java.io.IOException;
import java.nio.file.Path;

import javax.lang.model.element.Modifier;

import org.eclipse.jetty.server.Server;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;

public class RootFactoryTemplate {

    private final String projectName;
    private final Path targetDir;
    private final String packageName;

    public RootFactoryTemplate(String projectName, Path targetDir, String packageName) {
        this.projectName = projectName;
        this.targetDir = targetDir;
        this.packageName = packageName;
    }

    public TypeSpec generate(){
        String name = projectName + "RootFactory";

        ParameterizedTypeName serverAttributeType = ParameterizedTypeName.get(ClassName.get(FactoryAttribute.class), ClassName.get(Server.class), ParameterizedTypeName.get(ClassName.get(JettyServerFactory.class), ClassName.bestGuess(name)));
        FieldSpec serverAttribute = FieldSpec.builder(serverAttributeType,"jettyServer", Modifier.PUBLIC, Modifier.FINAL).initializer("new FactoryAttribute<>()")
                .build();

        MethodSpec create = MethodSpec.methodBuilder("createImpl")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(Server.class))
                .addStatement("return jettyServer.instance()")
                .addAnnotation(Override.class)
                .build();

        TypeSpec rootFactory = TypeSpec.classBuilder(name)
                .superclass((ParameterizedTypeName.get(ClassName.get(SimpleFactoryBase.class),
                        ClassName.get(Server.class),(ClassName.bestGuess(name)))))
                .addModifiers(Modifier.PUBLIC)
                .addField(serverAttribute)
                .addMethod(create)
                .addJavadoc("Root factory of the project")
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, rootFactory)
                .build();
        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootFactory;
    }
    public String getName() {
        return "ServerRootFactory";
    }
}
