package de.factoryfx.factory.typescript.generator.ts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TsMethodCode {
    private final String code;
    private final List<TsClass> imports;

    public TsMethodCode(String code, List<TsClass> imports) {
        this.code = code;
        this.imports = imports;
    }

    public TsMethodCode(String code) {
        this(code, new ArrayList<>());
    }

    public void addImports(Set<TsClass> imports){
        imports.addAll(this.imports);
    }

    public String getCode(){
        return code;
    }
}

