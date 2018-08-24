package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodResultPrimitive implements TsMethodResult {

    private final String type;

    public TsMethodResultPrimitive(String type) {
        this.type = type;
    }

    @Override
    public void addImports(Set<TsClass> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return ": "+type;
    }
}
