package de.factoryfx.factory.typescript.generator.ts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TsMethodParameter {

    void addImport(Set<TsClass> imports);

    String construct();
}
