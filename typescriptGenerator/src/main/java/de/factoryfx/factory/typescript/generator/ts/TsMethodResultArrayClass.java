package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodResultArrayClass implements TsMethodResult{

    private final TsClass type;

    public TsMethodResultArrayClass(TsClass type) {
        this.type = type;
    }

    @Override
    public void addImports(Set<TsClass> imports) {
        imports.add(type);
    }

    @Override
    public String construct() {
        return ": "+type.getName()+"[]";
    }
}
