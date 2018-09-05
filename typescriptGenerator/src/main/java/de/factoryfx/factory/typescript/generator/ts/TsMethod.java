package de.factoryfx.factory.typescript.generator.ts;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsMethod {

    public final String name;
    public final TsMethodCode code;
    public final List<TsMethodParameter> parameters;
    public final TsMethodResult result;
    public final String scope;

    public TsMethod(String name, List<TsMethodParameter> parameters, TsMethodResult result, TsMethodCode code) {
        this(name,parameters,result,code,"public");
    }

    public TsMethod(String name, List<TsMethodParameter> parameters, TsMethodCode code) {
        this(name,parameters,new TsMethodResultVoid(),code,"public");
    }

    public TsMethod(String name, List<TsMethodParameter> parameters, TsMethodResult result, TsMethodCode code,String scope) {
        this.name = name;
        this.code = code;
        this.parameters = parameters;
        this.scope=scope;
        this.result= result;
    }

    public String construct(){
        String indent="    ";
        return
            indent+scope +" "+ name+"("+parameters.stream().map(TsMethodParameter::construct).collect(Collectors.joining(", "))+")"+result.construct()+"{\n"+
                    indent+indent+code.getCode().replace("\n","\n"+indent+indent).trim()+"\n"+
            indent+"}\n";
    }

    public void addImports(Set<TsClassFile> imports){
        result.addImport(imports);
        for (TsMethodParameter p : parameters) {
            p.addImport(imports);
        }
        code.addImports(imports);
    }
}
