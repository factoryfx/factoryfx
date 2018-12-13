package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodResult{

    private final TsType type;

    public TsMethodResult(TsType type) {
        this.type = type;
    }

    public String construct(){
        return ": "+type.construct();
    }

    public void addImport(Set<TsFile> imports) {
        type.addImport(imports);
    }
}
