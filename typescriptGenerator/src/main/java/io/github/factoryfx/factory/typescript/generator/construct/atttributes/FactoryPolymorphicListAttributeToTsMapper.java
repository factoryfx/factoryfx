package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Map;
import java.util.Set;

public class FactoryPolymorphicListAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final TsTypeClass dataType;

    public FactoryPolymorphicListAttributeToTsMapper(TsTypeClass dataType) {
        this.dataType = dataType;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        return new TsTypeArray(dataType);
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        return ("this."+attributeVariableName+"=<Data[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap,this);\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        return "result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
