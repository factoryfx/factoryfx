package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
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
    public TsType getTsType(AttributeMetadata metadata) {
        Class<?> referenceClass = metadata.referenceClass;
        if (referenceClass==null){
            return new TsTypeClass(dataType);
        }
        return new TsTypeClass(dataToConfigTs.get(referenceClass));
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass(metadata);
        jsonImports.add(dataClass);
        return "this."+metadata.attributeVariableName+"=<"+dataClass.getName()+">dataCreator.createData(json."+metadata.attributeVariableName+".v,idToDataMap,this);\n";

    }

    private TsFile getTsClass(AttributeMetadata metadata) {
        Class<?> referenceClass = metadata.referenceClass;
        if (referenceClass==null){
            return dataType;
        }
        return dataToConfigTs.get(referenceClass);
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        TsFile dataClass = getTsClass(metadata);
        jsonImports.add(dataClass);
        return "result."+metadata.attributeVariableName+"=this.mapAttributeDataToJson(idToDataMap,this."+metadata.attributeVariableName+");\n";
    }
}
