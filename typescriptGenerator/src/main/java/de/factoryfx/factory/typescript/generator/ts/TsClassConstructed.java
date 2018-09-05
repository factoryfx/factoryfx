package de.factoryfx.factory.typescript.generator.ts;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsClassConstructed  extends TsClassFile {

    public TsClassFile parent;
    public List<TsAttribute> attributes=new ArrayList<>();
    public List<TsMethod> methods=new ArrayList<>();
    private String staticInitialisationMethodName;
    private TsClassFile extendsFrom;
    private boolean abstractClass;

    public TsClassConstructed(String name, Path targetPath) {
        super(name,targetPath);
    }

    public String extendsString(){
        if (parent!=null) {
            return " extends "+parent.getName();

        }
        return "";
    }

    public String generateTsFile() {
        String extendsString="";
        if (extendsFrom!=null){
            extendsString=" extends " + extendsFrom.getName();
        }

        String abstractClassString="";
        if (abstractClass) {
            abstractClassString="abstract ";
        }

        String result=
            getImports().stream().map((tsClass)-> constructImport(tsClass)).collect(Collectors.joining("\n"))+"\n\n"+
                    "export default "+abstractClassString+"class "+getName()+" "+extendsString()+extendsString+" {\n" +
                    "\n" +
                    attributes.stream().map(tsAttribute -> "    "+tsAttribute.constructClassDeclaration()).collect(Collectors.joining("\n"))+
                    "\n" +
                    methods.stream().map(TsMethod::construct).collect(Collectors.joining("\n"))+
                    "\n" +
                    "}";

        if (staticInitialisationMethodName!=null){
            result+=";\n"+getName()+"."+staticInitialisationMethodName+"();";
        }
        return result.replace("\n\n\n","\n\n");

    }

    private String constructImport(TsClassFile tsClass) {
        String importPath = tsClass.getRelativePathToFileName(this).toString().replace("\\", "/").replace(".ts", "");
        if (!importPath.startsWith(".")) {
            importPath = "./"+importPath;

        }
        return "import "+tsClass.getName()+" from \""+ importPath +"\";";
    }

    private Set<TsClassFile> getImports(){
        HashSet<TsClassFile> imports = new HashSet<>();
        if (parent!=null){
            imports.add(parent);
        }
        for (TsMethod method : methods) {
            method.addImports(imports);
        }
        for (TsAttribute attribute : attributes) {

            attribute.addImport(imports);
        }
        if (extendsFrom!=null){
            imports.add(extendsFrom);
        }
        imports.remove(this);
        return imports;
    }
    
    public void addStaticInitialisation(String methodName){
        staticInitialisationMethodName=methodName;
    }

    public void extendsFrom(TsClassFile extendsFrom) {
        this.extendsFrom=extendsFrom;
    }

    public void abstractClass(){
        abstractClass=true;
    }
}
