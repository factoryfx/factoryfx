package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.HashSet;
import java.util.Objects;
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

    public void addImports(Set<TsFile> allImports){
        this.imports.stream().filter(Objects::nonNull).forEach(allImports::add);
    }

    public String getCode(){
        return code;
    }
}

