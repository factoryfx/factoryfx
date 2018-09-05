package de.factoryfx.factory.typescript.generator.construct;

import de.factoryfx.data.Data;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataCreatorTs {

    private final List<Class<? extends Data>> allDataClasses;
    private final HashMap<Class<? extends Data>, TsClassConstructed> dataToDataConfigTs;
    private final TsClassFile dataClass;
    private final Path targetPath;

    public DataCreatorTs(List<Class<? extends Data>> allDataClasses, HashMap<Class<? extends Data>, TsClassConstructed> dataToDataConfigTs, TsClassFile dataClass, Path targetPath) {
        this.allDataClasses = allDataClasses;
        this.dataToDataConfigTs = dataToDataConfigTs;
        this.dataClass = dataClass;
        this.targetPath = targetPath;
    }

    public TsClassFile construct(){
        TsClassConstructed constructed = new TsClassConstructed("DataCreator",  targetPath);

        StringBuilder typeMappingCode=new StringBuilder();
        typeMappingCode.append("if (!json) return null;\n");
        typeMappingCode.append("let clazz=json['@class'];\n");
        typeMappingCode.append("if (typeof json === 'string'){\n");
        typeMappingCode.append("    return idToDataMap[json];\n");
        typeMappingCode.append("}\n");
        for (Class<? extends Data> allDataClass : allDataClasses) {
            typeMappingCode.append("if (clazz==='"+allDataClass.getName()+"'){\n");
            String typeName = allDataClass.getSimpleName();
            typeMappingCode.append("    let result: "+ typeName +"= new "+ typeName +"();\n");
            typeMappingCode.append("    result.mapFromJson(json,idToDataMap,this);\n");
            typeMappingCode.append("    return result;\n");
            typeMappingCode.append("}\n");
        }
        typeMappingCode.append("return null;");

        constructed.methods.add(new TsMethod("createData",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any"))),new TsMethodResult(new TsTypeClass(dataClass)),
                new TsMethodCode(typeMappingCode.toString(),allDataClasses.stream().map(dataToDataConfigTs::get).collect(Collectors.toList())),"public"));


        StringBuilder typeListMappingCode=new StringBuilder();
        typeListMappingCode.append("let result: Data[]=[];\n");
        typeListMappingCode.append("for (let entry of json) {\n" +
                "    result.push(this.createData(entry,idToDataMap));\n" +
                "}\n");
        typeListMappingCode.append("return result;");

        constructed.methods.add(new TsMethod("createDataList",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any"))),new TsMethodResult(new TsTypeArray(new TsTypeClass(dataClass))),
                new TsMethodCode(typeListMappingCode.toString(),allDataClasses.stream().map(dataToDataConfigTs::get).collect(Collectors.toList())),"public"));

        return constructed;
    }
}
