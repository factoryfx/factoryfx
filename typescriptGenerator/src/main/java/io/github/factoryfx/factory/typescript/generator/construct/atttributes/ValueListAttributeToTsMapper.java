package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.metadata.AttributeMetadata;
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
    public TsType getTsType(AttributeMetadata metadata) {
        return new TsTypeArray(this.tsType);
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
