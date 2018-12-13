package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsMethodResultVoid extends TsMethodResult {

    public TsMethodResultVoid() {
        super(null);
    }

    @Override
    public String construct(){
        return "";
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        //nothing
    }
}
