package de.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public interface TsType  {
    void addImport(Set<TsFile> imports);
    String construct();
}
