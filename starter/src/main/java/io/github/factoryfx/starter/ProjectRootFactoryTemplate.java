package io.github.factoryfx.starter;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import io.github.factoryfx.factory.FactoryBase;

import javax.lang.model.element.Modifier;

public class ProjectRootFactoryTemplate {

    private final String projectName;

    public ProjectRootFactoryTemplate(String projectName) {
        this.projectName = projectName;
    }

    public TypeSpec generate(){
        TypeSpec helloWorld = TypeSpec.classBuilder(projectName+"RootFactory")
                .superclass((ParameterizedTypeName.get(ClassName.get(FactoryBase.class),
                        ClassName.get(FactoryBase.class))))
                .addModifiers(Modifier.PUBLIC)
                .build();

        return  helloWorld;
    }
}
