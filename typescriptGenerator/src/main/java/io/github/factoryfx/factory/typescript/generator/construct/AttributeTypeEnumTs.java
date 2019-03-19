package io.github.factoryfx.factory.typescript.generator.construct;

import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.TsEnumConstructed;

import java.nio.file.Path;

public class AttributeTypeEnumTs {
    private final AttributeToTsMapperManager attributeToTsMapperManager;
    private final Path targetPath;

    public AttributeTypeEnumTs(AttributeToTsMapperManager attributeToTsMapperManager, Path targetPath) {
        this.attributeToTsMapperManager = attributeToTsMapperManager;
        this.targetPath=targetPath;
    }

    public TsEnumConstructed construct() {
        TsEnumConstructed constructed = new TsEnumConstructed("AttributeType","" ,targetPath);
        constructed.addEnumValues(attributeToTsMapperManager.getAttributeTypeValues());
        return constructed;
    }

}
