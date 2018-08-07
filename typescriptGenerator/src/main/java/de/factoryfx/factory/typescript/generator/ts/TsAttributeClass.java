package de.factoryfx.factory.typescript.generator.ts;

import java.util.Optional;
import java.util.Set;

public class TsAttributeClass implements TsAttribute {

    private final String name;
    private final TsClass type;

    public TsAttributeClass(String name, TsClass type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addImport(Set<TsClass> imports) {
        imports.add(type);
    }

    @Override
    public String construct() {
        return "public "+name+": "+type.getName()+";";
    }
}
