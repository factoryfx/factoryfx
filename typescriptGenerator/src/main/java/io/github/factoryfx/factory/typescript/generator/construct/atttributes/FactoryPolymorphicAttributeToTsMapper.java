package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.TsClassConstructed;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeClass;

import java.util.Map;
import java.util.Set;

public class FactoryPolymorphicAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final TsTypeClass dataType;

    public FactoryPolymorphicAttributeToTsMapper(TsTypeClass dataType) {
        this.dataType = dataType;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        return dataType;
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
//        Class referenceClass = ((FactoryBaseAttribute) attribute).internal_getReferenceClass();
////        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
////        jsonImports.add(dataClass);
        return "this."+attributeVariableName+"=<Data>dataCreator.createData(json."+attributeVariableName+".v,idToDataMap,this);\n";

    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
//        Class referenceClass = ((FactoryBaseAttribute) attribute).internal_getReferenceClass();
//        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
//        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
