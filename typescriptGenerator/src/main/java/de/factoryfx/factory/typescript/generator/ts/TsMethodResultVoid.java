package de.factoryfx.factory.typescript.generator.ts;

import java.util.List;
import java.util.Set;

public class TsMethodResultVoid implements TsMethodResult {

    public TsMethodResultVoid() {

    }

    @Override
    public void addImports(Set<TsClass> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return "";
    }
}
