package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodParameter {
    private final String name;
    private final TsType type;

    public TsMethodParameter(String name, TsType type) {
        this.name = name;
        this.type = type;
    }

    public void addImport(Set<TsFile> imports){
        type.addImport(imports);
    }

    public String construct(){
        return name+": "+type.construct();
    }
}
