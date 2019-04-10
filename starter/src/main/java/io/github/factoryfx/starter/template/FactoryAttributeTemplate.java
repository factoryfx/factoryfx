package io.github.factoryfx.starter.template;

import com.squareup.javapoet.*;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;

public class FactoryAttributeTemplate {
    private final Path targetDir;
    private final RootFactoryTemplate rootFactoryTemplate;
    private final String packageName ;

    public FactoryAttributeTemplate(RootFactoryTemplate rootFactoryTemplate, Path targetDir, String packageName) {
        this.targetDir = targetDir;
        this.rootFactoryTemplate = rootFactoryTemplate;
        this.packageName = packageName;
    }

    public void generateFile(){
        TypeVariableName typeVariableNameL = TypeVariableName.get("L");
        TypeVariableName typeVariableNameF = TypeVariableName.get("F",ParameterizedTypeName.get(ClassName.get(FactoryBase.class),typeVariableNameL,ClassName.bestGuess(rootFactoryTemplate.generate().name)));

        TypeSpec factoryAttribute = TypeSpec.classBuilder("FactoryAttribute")
                .superclass(ParameterizedTypeName.get(ClassName.get(FactoryAttribute.class),
                        ClassName.bestGuess(rootFactoryTemplate.generate().name),typeVariableNameL,typeVariableNameF))
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(typeVariableNameL)
                .addTypeVariable(typeVariableNameF)
                .addJavadoc("adds ServerRootFactory als generic type")
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, factoryAttribute)
                .build();

        try {
            javaFile.writeTo(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
