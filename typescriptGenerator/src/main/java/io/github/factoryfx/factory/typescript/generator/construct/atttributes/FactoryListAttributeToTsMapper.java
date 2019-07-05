package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Map;
import java.util.Set;

public class FactoryListAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs;
    private final TsFile dataType;

    public FactoryListAttributeToTsMapper(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs, TsFile dataType) {
        this.dataToConfigTs = dataToConfigTs;
        this.dataType = dataType;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((FactoryListBaseAttribute) attribute).internal_getReferenceClass();
        if (referenceClass==null){
            return new TsTypeClass(dataType);
        }
        return new TsTypeArray(new TsTypeClass(dataToConfigTs.get(referenceClass)));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass((FactoryListBaseAttribute) attribute);
        jsonImports.add(dataClass);
        return ("this."+attributeVariableName+"=<"+dataClass.getName()+"[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap,this);\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass((FactoryListBaseAttribute) attribute);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n";
    }

    private TsFile getTsClass(FactoryListBaseAttribute attribute) {
        Class referenceClass = attribute.internal_getReferenceClass();
        if (referenceClass==null){
            return dataType;
        }
        return dataToConfigTs.get(referenceClass);
    }
}
