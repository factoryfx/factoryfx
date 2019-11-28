package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;

import java.util.Set;

public interface AttributeToTsMapper {
    TsType getTsType(AttributeMetadata metadata);

    String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports);

    String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports);
}
