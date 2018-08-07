package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsAttributeArrayClass implements TsAttribute {

    private final String name;
    private final TsClass type;

    public TsAttributeArrayClass(String name, TsClass type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClass> imports) {
        imports.add(type);
    }

    @Override
    public String construct() {
        return "public "+name+": "+type.getName()+"[];";
    }
}
