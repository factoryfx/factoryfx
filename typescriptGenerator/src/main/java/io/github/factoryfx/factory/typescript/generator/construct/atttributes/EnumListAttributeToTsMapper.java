package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.Set;

public class EnumListAttributeToTsMapper implements AttributeToTsMapper {
    private final Set<TsEnumConstructed> tsEnums;

    public EnumListAttributeToTsMapper(Set<TsEnumConstructed> tsEnums) {
        this.tsEnums = tsEnums;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        for (TsEnumConstructed tsEnum : tsEnums) {
            if (tsEnum.getName().equals(((EnumListAttribute<?>) attribute).internal_getEnumClass().getSimpleName())){
                return new TsTypeArray(new TsTypeClass(tsEnum));
            }
        }
        return null;
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        return ("this."+attributeVariableName+"=json."+attributeVariableName+";\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        return "result."+attributeVariableName+"=this."+attributeVariableName+";\n";
    }
}
