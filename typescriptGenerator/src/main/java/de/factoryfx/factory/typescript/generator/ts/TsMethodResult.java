package de.factoryfx.factory.typescript.generator.ts;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TsMethodResult {
    void addImports(Set<TsClass> imports);
    String construct();
}
