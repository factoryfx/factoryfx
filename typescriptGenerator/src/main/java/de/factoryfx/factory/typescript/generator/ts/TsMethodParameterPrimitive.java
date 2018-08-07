package de.factoryfx.factory.typescript.generator.ts;

import java.util.Optional;
import java.util.Set;

public class TsMethodParameterPrimitive implements TsMethodParameter {

    private final String name;
    private final String type;

    public TsMethodParameterPrimitive(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClass> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return name+": "+type;
    }
}
