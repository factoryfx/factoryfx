package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.TsClassConstructed;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeClass;

import java.util.Map;
import java.util.Set;

public class ReferenceAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs;

    public ReferenceAttributeToTsMapper(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs) {
        this.dataToConfigTs = dataToConfigTs;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((FactoryReferenceBaseAttribute) attribute).internal_getReferenceClass();
        return new TsTypeClass(dataToConfigTs.get(referenceClass));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryReferenceBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return "this."+attributeVariableName+"=<"+dataClass.getName()+">dataCreator.createData(json."+attributeVariableName+".v,idToDataMap);\n";

    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((FactoryReferenceBaseAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
