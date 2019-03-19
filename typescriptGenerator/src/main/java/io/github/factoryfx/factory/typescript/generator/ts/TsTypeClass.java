package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsTypeClass implements TsType {
    private final TsFile type;
    private final List<TsType> genericParameters;

    public TsTypeClass(TsFile type, TsType... genericParameters) {
        this.type = type;
        this.genericParameters = List.of(genericParameters);
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        imports.add(type);
        for (TsType genericParameter : genericParameters) {
            genericParameter.addImport(imports);
        }
    }

    @Override
    public String construct() {
        String name = type.getName();
        String genericParametersString="";
        if (!genericParameters.isEmpty()) {
            genericParametersString="<"+genericParameters.stream().map(TsType::construct).collect(Collectors.joining(","))+">";
        }
        return name+genericParametersString;
    }
}
