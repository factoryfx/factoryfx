package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Set;

public class EnumListAttributeToTsMapper implements AttributeToTsMapper {
    private final Set<TsEnumConstructed> tsEnums;

    public EnumListAttributeToTsMapper(Set<TsEnumConstructed> tsEnums) {
        this.tsEnums = tsEnums;
    }

    @Override
    public TsType getTsType(AttributeMetadata metadata) {
        for (TsEnumConstructed tsEnum : tsEnums) {
            if (tsEnum.getName().equals(metadata.enumClass.getSimpleName())){
                return new TsTypeArray(new TsTypeClass(tsEnum));
            }
        }
        return null;
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        return ("this."+metadata.attributeVariableName+"=json."+metadata.attributeVariableName+";\n");
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        return "result."+metadata.attributeVariableName+"=this."+metadata.attributeVariableName+";\n";
    }
}
