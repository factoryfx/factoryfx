package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public interface TsAttribute {

    void addImport(Set<TsClass> imports);

    String construct();
}
