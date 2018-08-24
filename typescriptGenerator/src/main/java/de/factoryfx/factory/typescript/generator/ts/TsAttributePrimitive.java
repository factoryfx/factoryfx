package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsAttributePrimitive implements TsAttribute {

    private final String name;
    private final String type;

    public TsAttributePrimitive(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClass> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return "public "+name+": "+type+";";
    }

}
