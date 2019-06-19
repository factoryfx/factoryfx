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

public class FactoryAttributeToTsMapper<R extends FactoryBase<?,R>> implements AttributeToTsMapper {
    private final Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs;
    private final TsFile dataType;

    public FactoryAttributeToTsMapper(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToConfigTs, TsFile dataType) {
        this.dataToConfigTs = dataToConfigTs;
        this.dataType = dataType;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((FactoryBaseAttribute) attribute).internal_getReferenceClass();
        if (referenceClass==null){
            return new TsTypeClass(dataType);
        }
        return new TsTypeClass(dataToConfigTs.get(referenceClass));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass((FactoryBaseAttribute) attribute);
        jsonImports.add(dataClass);
        return "this."+attributeVariableName+"=<"+dataClass.getName()+">dataCreator.createData(json."+attributeVariableName+".v,idToDataMap,this);\n";

    }

    private TsFile getTsClass(FactoryBaseAttribute attribute) {
        Class referenceClass = attribute.internal_getReferenceClass();
        if (referenceClass==null){
            return dataType;
        }
        return dataToConfigTs.get(referenceClass);
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass((FactoryBaseAttribute) attribute);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
