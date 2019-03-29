package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeArray;

import java.util.Set;

public class ValueListAttributeToTsMapper implements AttributeToTsMapper {
    private final TsType tsType;

    public ValueListAttributeToTsMapper(TsType tsType) {
        this.tsType = tsType;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        return new TsTypeArray(this.tsType);
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
