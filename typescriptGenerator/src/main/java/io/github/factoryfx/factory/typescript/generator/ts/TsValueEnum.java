package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsValueEnum  implements TsValue {
    private final String value;
    private final TsFile enumConstructed;

    public TsValueEnum(String value, TsFile enumConstructed){
        this.value=value;
        this.enumConstructed = enumConstructed;
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        imports.add(enumConstructed);
    }

    @Override
    public String construct() {
        return enumConstructed.getName()+"."+value;
    }

}
