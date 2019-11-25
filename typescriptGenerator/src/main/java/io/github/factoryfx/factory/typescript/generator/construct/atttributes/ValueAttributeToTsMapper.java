package io.github.factoryfx.factory.typescript.generator.construct.atttributes;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.TsFile;
import io.github.factoryfx.factory.typescript.generator.ts.TsType;

import java.util.Set;

public class ValueAttributeToTsMapper implements AttributeToTsMapper {

    public final TsType tsType;
    public final String mapFromJsonMethodname;
    public final String mapToJsonMethodname;

    public ValueAttributeToTsMapper(TsType tsType, String mapFromJsonMethodname, String mapToJsonMethodname) {
        this.tsType = tsType;
        this.mapFromJsonMethodname = mapFromJsonMethodname;
        this.mapToJsonMethodname = mapToJsonMethodname;
    }

    public ValueAttributeToTsMapper(TsType tsType) {
        this(tsType,null,null);
    }


    public TsType getTsType(AttributeMetadata metadata){
        return tsType;
    }

    @Override
    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        String result="this."+metadata.attributeVariableName+"=";
        result += (mapFromJsonMethodname != null ? "this." + mapFromJsonMethodname + "(" : "") + "json." + metadata.attributeVariableName + ".v" + (mapFromJsonMethodname != null ? ")" : "");
        result += ";\n";
        return result;
    }

    @Override
    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        String result = "result."+metadata.attributeVariableName+"=this.mapAttributeValueToJson(";
        result += (mapToJsonMethodname!=null?"this."+mapToJsonMethodname+"(":"")+"this."+metadata.attributeVariableName+(mapToJsonMethodname!=null?")":"");
        result += ");\n";
        return result;
    }
}
