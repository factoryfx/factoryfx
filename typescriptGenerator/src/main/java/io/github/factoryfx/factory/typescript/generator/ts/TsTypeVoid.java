package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsTypeVoid implements TsType {

    public TsTypeVoid() {

    }

    @Override
    public void addImport(Set<TsFile> imports) {
       //nothing
    }

    @Override
    public String construct() {
        return "void";
    }
}
