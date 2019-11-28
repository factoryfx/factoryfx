package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeArray;
import io.github.factoryfx.factory.typescript.generator.ts.TsTypeClass;

import java.util.Set;

public class ViewListAttributeToTsMapper implements AttributeToTsMapper {

    public final TsType dataType;

    public ViewListAttributeToTsMapper(TsFile dataType) {
        this.dataType = new TsTypeClass(dataType);
    }


    public TsType getTsType(AttributeMetadata metadata){
        return new TsTypeArray(dataType);
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        return "";
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        return "";
    }
}
