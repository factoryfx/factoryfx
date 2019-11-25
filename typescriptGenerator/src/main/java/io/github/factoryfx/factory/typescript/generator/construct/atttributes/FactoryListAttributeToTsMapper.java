package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
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
    public TsType getTsType(AttributeMetadata metadata) {
        Class<?> referenceClass = metadata.referenceClass;
        if (referenceClass==null){
            return new TsTypeArray(new TsTypeClass(dataType));
        }
        return new TsTypeArray(new TsTypeClass(dataToConfigTs.get(referenceClass)));
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass(metadata);
        jsonImports.add(dataClass);
        return ("this."+metadata.attributeVariableName+"=<"+dataClass.getName()+"[]>dataCreator.createDataList(json."+metadata.attributeVariableName+",idToDataMap,this);\n");
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass(metadata);
        jsonImports.add(dataClass);
        return "result."+metadata.attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+metadata.attributeVariableName+");\n";
    }

    private TsFile getTsClass(AttributeMetadata metadata) {
        Class<?> referenceClass = metadata.referenceClass;
        if (referenceClass==null){
            return dataType;
        }
        return dataToConfigTs.get(referenceClass);
    }
}
