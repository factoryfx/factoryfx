package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsTypeArray implements TsType {
    private final TsType type;
    public TsTypeArray(TsType type) {
        this.type = type;
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        type.addImport(imports);
    }

    @Override
    public String construct() {
        return type.construct()+"[]";
    }
}
