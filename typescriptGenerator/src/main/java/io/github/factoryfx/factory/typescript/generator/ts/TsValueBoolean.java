package io.github.factoryfx.factory.typescript.generator.ts;


import java.util.Set;

public class TsValueBoolean implements TsValue {
    private final boolean value;

    public TsValueBoolean(boolean value){
        this.value=value;
    }

    public void addImport(Set<TsFile> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return ""+value;
    }
}
