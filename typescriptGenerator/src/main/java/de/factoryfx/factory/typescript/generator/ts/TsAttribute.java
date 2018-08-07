package de.factoryfx.factory.typescript.generator.ts;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface TsAttribute {

    void addImport(Set<TsClass> imports);

    String construct();
}
