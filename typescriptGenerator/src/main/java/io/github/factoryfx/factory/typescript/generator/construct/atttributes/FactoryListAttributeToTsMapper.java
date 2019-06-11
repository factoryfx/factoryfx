package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Map;
import java.util.Set;

public class FactoryListAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs;

    public FactoryListAttributeToTsMapper(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs) {
        this.dataToConfigTs = dataToConfigTs;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((FactoryListBaseAttribute) attribute).internal_getReferenceClass();
        return new TsTypeArray(new TsTypeClass(dataToConfigTs.get(referenceClass)));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryListBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return ("this."+attributeVariableName+"=<"+dataClass.getName()+"[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap,this);\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryListBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
