package de.factoryfx.factory.typescript.generator.construct.atttributes;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.factory.typescript.generator.ts.TsEnumConstructed;
import de.factoryfx.factory.typescript.generator.ts.TsFile;
import de.factoryfx.factory.typescript.generator.ts.TsType;
import de.factoryfx.factory.typescript.generator.ts.TsTypeClass;

import java.util.Set;

public class EnumAttributeToTsMapper implements AttributeToTsMapper {
    private final Set<TsEnumConstructed> tsEnums;

    public EnumAttributeToTsMapper(Set<TsEnumConstructed> tsEnums) {
        this.tsEnums = tsEnums;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        for (TsEnumConstructed tsEnum : tsEnums) {
            if (tsEnum.getName().equals(((EnumAttribute<?>) attribute).internal_getEnumClass().getSimpleName())){
                return new TsTypeClass(tsEnum);
            }
        }
        return null;
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        String enumName = ((EnumAttribute<?>) attribute).internal_getEnumClass().getSimpleName();
        return "this."+attributeVariableName+"="+enumName+".fromJson(json." + attributeVariableName + ".v);\n";
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        String enumName = ((EnumAttribute<?>) attribute).internal_getEnumClass().getSimpleName();
        return "result."+attributeVariableName+"=this.mapAttributeValueToJson("+enumName+".toJson(this." + attributeVariableName + "));\n";
    }
}
