package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsTypeNull implements TsType {

    public TsTypeNull() {

    }

    @Override
    public void addImport(Set<TsFile> imports) {
       //nothing
    }

    @Override
    public String construct() {
        return "null";
    }
}
