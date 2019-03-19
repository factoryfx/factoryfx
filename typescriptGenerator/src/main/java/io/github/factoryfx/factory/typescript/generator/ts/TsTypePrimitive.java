package io.github.factoryfx.factory.typescript.generator.ts;

import java.util.Set;

public class TsTypePrimitive implements TsType {

    public static final TsTypePrimitive BOOLEAN=new TsTypePrimitive("boolean");
    public static final TsTypePrimitive NUMBER=new TsTypePrimitive("number");
    public static final TsTypePrimitive STRING=new TsTypePrimitive("string");
    public static final TsTypePrimitive DATE=new TsTypePrimitive("Date");
    public static final TsTypePrimitive BIGINT=new TsTypePrimitive("bigint");

    private final String type;
    public TsTypePrimitive(String type) {
        this.type = type;
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return type;
    }
}
