package de.factoryfx.factory.typescript.generator.ts;

import java.util.HashSet;
import java.util.Set;

public class TsMethodCode {
    private final String code;
    private final Set<TsFile> imports;

    public TsMethodCode(String code, Set<TsFile> imports) {
        this.code = code;
        this.imports = imports;
    }

    public TsMethodCode(String code) {
        this(code, new HashSet<>());
    }

    public void addImports(Set<TsFile> imports){
        imports.addAll(this.imports);
    }

    public String getCode(){
        return code;
    }
}

