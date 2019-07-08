package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsMethodResult{

    private final List<TsType> types;

    public TsMethodResult(TsType type) {
        this.types = List.of(type);
    }

    public TsMethodResult(TsType type, TsType... alternativeTypes) {
        this.types = new ArrayList<>();
        this.types.add(type);
        if (alternativeTypes.length>0) {
            this.types.addAll(Arrays.asList(alternativeTypes));
        }
    }

    public String construct(){
        return ": "+types.stream().map(TsType::construct).collect(Collectors.joining("|"));
    }

    public void addImport(Set<TsFile> imports) {
        for (TsType type : types) {
            type.addImport(imports);
        }
    }
}
