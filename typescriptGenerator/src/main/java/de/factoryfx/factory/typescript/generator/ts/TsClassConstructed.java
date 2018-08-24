package de.factoryfx.factory.typescript.generator.ts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsClassConstructed  extends TsClass  {

    public TsClass parent;
    public List<TsAttribute> attributes=new ArrayList<>();
    public List<TsMethod> methods=new ArrayList<>();

    public TsClassConstructed(String name) {
        super(name);
    }

    public String extendsString(){
        if (parent!=null) {
            return " extends "+parent.getName();

        }
        return "";
    }

    public String generateTsFile() {
        return
            getImports().stream().map((tsClass)->"import "+tsClass.getName()+" from \"./"+tsClass.getName()+"\";").collect(Collectors.joining("\n"))+"\n\n"+
            "export default class "+getName()+" "+extendsString()+" {\n" +
                    "\n" +
                    attributes.stream().map(tsAttribute -> "    "+tsAttribute.construct()).collect(Collectors.joining("\n"))+
                    "\n\n" +
                    methods.stream().map(TsMethod::construct).collect(Collectors.joining("\n"))+
                    "\n\n" +
            "}";
    }

    private Set<TsClass> getImports(){
        HashSet<TsClass> imports = new HashSet<>();
        if (parent!=null){
            imports.add(parent);
        }
        for (TsMethod method : methods) {
            method.addImports(imports);
        }
        for (TsAttribute attribute : attributes) {

            attribute.addImport(imports);
        }
        return imports;
    }

}
