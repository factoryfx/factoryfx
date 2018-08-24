package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodResultClass implements TsMethodResult{

    private final TsClass type;

    public TsMethodResultClass(TsClass type) {
        this.type = type;
    }

    @Override
    public void addImports(Set<TsClass> imports) {
        imports.add(type);
    }

    @Override
    public String construct() {
        return ": "+type.getName();
    }
}
