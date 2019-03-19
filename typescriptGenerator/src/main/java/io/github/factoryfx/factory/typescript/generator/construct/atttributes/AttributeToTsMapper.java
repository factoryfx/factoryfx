package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;

import java.util.Set;

public interface AttributeToTsMapper {
    TsType getTsType(Attribute<?,?> attribute);

    String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports);

    String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports);
}
