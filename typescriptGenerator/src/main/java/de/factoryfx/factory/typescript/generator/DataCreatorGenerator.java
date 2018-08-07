package de.factoryfx.factory.typescript.generator;

import de.factoryfx.data.Data;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataCreatorGenerator {

    private final List<Class<? extends Data>> allDataClasses;
    private final HashMap<Class<? extends Data>, TsClassConstructed> dataToTs;
    private final TsClass dataClass;

    public DataCreatorGenerator(List<Class<? extends Data>> allDataClasses, HashMap<Class<? extends Data>, TsClassConstructed> dataToTs, TsClass dataClass) {
        this.allDataClasses = allDataClasses;
        this.dataToTs = dataToTs;
        this.dataClass = dataClass;
    }

    public TsClass construct(){
        TsClassConstructed constructed = new TsClassConstructed("DataCreator");

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
                List.of(new TsMethodParameterPrimitive("json","any"),new TsMethodParameterPrimitive("idToDataMap","any")),new TsMethodResultClass(dataClass),
                new TsMethodCode(typeMappingCode.toString(),allDataClasses.stream().map(dataToTs::get).collect(Collectors.toList())),"public"));


        StringBuilder typeListMappingCode=new StringBuilder();
        typeListMappingCode.append("let result: Data[]=[];\n");
        typeListMappingCode.append("for (let entry of json) {\n" +
                "    result.push(this.createData(entry,idToDataMap));\n" +
                "}\n");
        typeListMappingCode.append("return result;");

        constructed.methods.add(new TsMethod("createDataList",
                List.of(new TsMethodParameterPrimitive("json","any"),new TsMethodParameterPrimitive("idToDataMap","any")),new TsMethodResultArrayClass(dataClass),
                new TsMethodCode(typeListMappingCode.toString(),allDataClasses.stream().map(dataToTs::get).collect(Collectors.toList())),"public"));

        return constructed;
    }
}
