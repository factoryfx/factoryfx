package io.github.factoryfx.factory.typescript.generator.construct;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataCreatorTs<R extends FactoryBase<?,R>> {

    private final Collection<Class<? extends FactoryBase<?,R>>> allDataClasses;
    private final HashMap<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToDataConfigTs;
    private final TsFile dataClass;
    private final Path targetPath;

    public DataCreatorTs(Collection<Class<? extends FactoryBase<?,R>>> allDataClasses, HashMap<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToDataConfigTs, TsFile dataClass, Path targetPath) {
        this.allDataClasses = allDataClasses;
        this.dataToDataConfigTs = dataToDataConfigTs;
        this.dataClass = dataClass;
        this.targetPath = targetPath;
    }

    public TsFile construct(){
        TsClassConstructed constructed = new TsClassConstructed("DataCreator", "",  targetPath);

        StringBuilder typeMappingCode=new StringBuilder();
        typeMappingCode.append("if (!json) return null;\n");
        typeMappingCode.append("let clazz=json['@class'];\n");
        typeMappingCode.append("if (typeof json === 'string'){\n");
        typeMappingCode.append("    return idToDataMap[json];\n");
        typeMappingCode.append("}\n");
        for (Class<? extends FactoryBase<?,?>> allDataClass : allDataClasses) {
            typeMappingCode.append("if (clazz==='").append(allDataClass.getName()).append("'){\n");
            String typeName = allDataClass.getSimpleName();
            typeMappingCode.append("    let result: ").append(typeName).append("= new ").append(typeName).append("();\n");
            typeMappingCode.append("    result.mapFromJson(json,idToDataMap,this,null);\n");
            typeMappingCode.append("    result.setParent(parent);\n");
            typeMappingCode.append("    return result;\n");
            typeMappingCode.append("}\n");
        }
        typeMappingCode.append("return null;");

        constructed.methods.add(new TsMethod("createData",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("parent",new TsTypeClass(dataClass))),new TsMethodResult(new TsTypeClass(dataClass)),
                new TsMethodCode(typeMappingCode.toString(),allDataClasses.stream().map(dataToDataConfigTs::get).collect(Collectors.toSet())),"public"));


        String typeListMappingCode =
                "let result: Data[]=[];\n" +
                "for (let entry of json) {\n" +
                "    result.push(this.createData(entry,idToDataMap,parent));\n" +
                "}\n" +
                "return result;";

        constructed.methods.add(new TsMethod("createDataList",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("parent",new TsTypeClass(dataClass))),new TsMethodResult(new TsTypeArray(new TsTypeClass(dataClass))),
                new TsMethodCode(typeListMappingCode,allDataClasses.stream().map(dataToDataConfigTs::get).collect(Collectors.toSet())),"public"));

        return constructed;
    }
}
