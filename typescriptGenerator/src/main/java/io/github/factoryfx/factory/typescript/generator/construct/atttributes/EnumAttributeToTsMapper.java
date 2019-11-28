package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.TsEnumConstructed;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeClass;

import java.util.Set;

public class EnumAttributeToTsMapper implements AttributeToTsMapper {
    private final Set<TsEnumConstructed> tsEnums;

    public EnumAttributeToTsMapper(Set<TsEnumConstructed> tsEnums) {
        this.tsEnums = tsEnums;
    }

    @Override
    public TsType getTsType(AttributeMetadata metadata) {
        for (TsEnumConstructed tsEnum : tsEnums) {
            if (tsEnum.getName().equals(metadata.enumClass.getSimpleName())){
                return new TsTypeClass(tsEnum);
            }
        }
        return null;
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        String enumName = metadata.enumClass.getSimpleName();
        return "this."+metadata.attributeVariableName+"="+enumName+".fromJson(json." + metadata.attributeVariableName + ".v);\n";
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        String enumName = metadata.enumClass.getSimpleName();
        return "result."+metadata.attributeVariableName+"=this.mapAttributeValueToJson("+enumName+".toJson(this." + metadata.attributeVariableName + "));\n";
    }
}
