package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Map;
import java.util.Set;

public class ReferenceListAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs;

    public ReferenceListAttributeToTsMapper(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs) {
        this.dataToConfigTs = dataToConfigTs;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((FactoryReferenceListBaseAttribute) attribute).internal_getReferenceClass();
        return new TsTypeArray(new TsTypeClass(dataToConfigTs.get(referenceClass)));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryReferenceListBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return ("this."+attributeVariableName+"=<"+dataClass.getName()+"[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap);\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryReferenceListBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
