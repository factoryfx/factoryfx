package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsConstructor {

    public final TsMethodCode code;
    public final List<TsMethodParameter> parameters;

    public TsConstructor(List<TsMethodParameter> parameters, TsMethodCode code) {
        this.code = code;
        this.parameters = parameters;
    }

    public String construct(){
        String indent="    ";
        return
            indent +"constructor("+parameters.stream().map(TsMethodParameter::construct).collect(Collectors.joining(", "))+")"+"{\n"+
                    indent+indent+code.getCode().replace("\n","\n"+indent+indent).trim()+"\n"+
            indent+"}\n";
    }

    public void addImports(Set<TsFile> imports){
        for (TsMethodParameter p : parameters) {
            p.addImport(imports);
        }
        code.addImports(imports);
    }
}
