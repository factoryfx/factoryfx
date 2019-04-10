package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsAttribute {

    private final String name;
    private final TsType type;
    private final boolean isStatic;
    private final boolean initialised;
    private final List<TsValue> constructorParameters;
    private final boolean readonly;

    public TsAttribute(String name, TsType type) {
        this.name = name;
        this.type = type;
        this.isStatic = false;
        this.initialised = false;
        this.constructorParameters = Collections.emptyList();
        this.readonly = false;
    }

    public TsAttribute(String name, TsType type, boolean isStatic, boolean initialised, boolean readonly, List<TsValue> constructorParameters) {
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.initialised = initialised;
        this.readonly = readonly;
        this.constructorParameters = constructorParameters;
    }

    public String constructClassDeclaration(){
        String constructorParametersString="";
        if (initialised){
            constructorParametersString= constructorParameters.stream().map(TsValue::construct).collect(Collectors.joining(","));
        }

        String initialisation="";
        if (initialised){
            initialisation="= new "+type.construct()+"("+constructorParametersString+")";
        }

        String staticString="";
        if (isStatic){
            staticString="static ";
        }

        String readonlyString="";
        if (readonly){
            readonlyString="readonly ";
        }
        return "public "+staticString+readonlyString+name+": "+type.construct()+initialisation+";";
    }

    public void addImport(Set<TsFile> imports) {
        for (TsValue constructorParameter : constructorParameters) {
            constructorParameter.addImport(imports);
        }
        type.addImport(imports);
    }
}
