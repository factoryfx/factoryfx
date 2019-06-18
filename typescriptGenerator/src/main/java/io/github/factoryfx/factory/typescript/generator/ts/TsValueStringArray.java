package io.github.factoryfx.factory.typescript.generator.ts;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsValueStringArray implements TsValue {
    private final List<String> values;

    public TsValueStringArray(List<String> values){
        this.values=values;
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
        return "["+values.stream().map(this::escapeTsString).collect(Collectors.joining(","))+"]";
    }
}
