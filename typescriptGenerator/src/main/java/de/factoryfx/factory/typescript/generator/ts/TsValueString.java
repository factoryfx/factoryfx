package de.factoryfx.factory.typescript.generator.ts;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import java.util.Set;

public class TsValueString implements TsValue {
    private final String value;

    public TsValueString(String value){
        this.value=value;
    }

    private static final Escaper TS_ESCAPER =
            Escapers.builder()
                    .addEscape('\\', "\\\\")
                    .addEscape('"', "\\\"")
                    .addEscape('\'', "\\\'")
                    .build();

    private String escapeTsString(String text){
        return "'"+TS_ESCAPER.escape(text)+"'";
    }

    @Override
    public void addImport(Set<TsFile> imports) {
        //nothing
    }

    @Override
    public String construct() {
        return escapeTsString(value);
    }
}
