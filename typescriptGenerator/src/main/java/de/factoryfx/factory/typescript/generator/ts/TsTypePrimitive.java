package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsTypePrimitive implements TsType {
    private final String type;
    public TsTypePrimitive(String type) {
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClassFile> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return type;
    }
}
