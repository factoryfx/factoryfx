package de.factoryfx.factory.typescript.generator.ts;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TsClassConstructed extends TsFile {

    public TsFile parent;
    public List<TsAttribute> attributes=new ArrayList<>();
    public List<TsMethod> methods=new ArrayList<>();
    private String staticInitialisationMethodName;
    private TsFile extendsFrom;
    private boolean abstractClass;

    public TsClassConstructed(String name, String subPath, Path targetPath) {
        super(name, subPath, targetPath);
    }

    public String extendsString(){
        if (parent!=null) {
            return " extends "+parent.getName();

        }
        return "";
    }

    @Override
    public String getContent() {
        return this.generateTsFile();
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
            getImports().stream().map(this::constructImport).collect(Collectors.joining("\n"))+"\n\n"+
                    "export "+abstractClassString+"class "+getName()+" "+extendsString()+extendsString+" {\n" +
                    "\n" +
                    attributes.stream().map(tsAttribute -> "    "+tsAttribute.constructClassDeclaration()).collect(Collectors.joining("\n"))+
                    "\n\n" +
                    methods.stream().map(TsMethod::construct).collect(Collectors.joining("\n"))+
                    "\n" +
                    "}";

        if (staticInitialisationMethodName!=null){
            result+="\n"+getName()+"."+staticInitialisationMethodName+"();";
        }
        return result.replace("\n\n\n","\n\n");

    }

    private String constructImport(TsFile tsFile) {
        String importPath = tsFile.getRelativePathToFileName(this).toString().replace("\\", "/").replace(".ts", "");
        if (!importPath.startsWith(".")) {
            importPath = "./"+importPath;

        }
        return "import { "+tsFile.getName()+" } from \""+ importPath +"\";";
    }

    private Set<TsFile> getImports(){
        Set<TsFile> imports = new LinkedHashSet<>();
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

    public void extendsFrom(TsFile extendsFrom) {
        this.extendsFrom=extendsFrom;
    }

    public void abstractClass(){
        abstractClass=true;
    }

}
