package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodParameterClass implements TsMethodParameter{

    private final String name;
    private final TsClass type;

    public TsMethodParameterClass(String name, TsClass type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClass> imports) {
        imports.add(type);
    }

    @Override
    public String construct() {
        return name+": "+type.getName();
    }
}
