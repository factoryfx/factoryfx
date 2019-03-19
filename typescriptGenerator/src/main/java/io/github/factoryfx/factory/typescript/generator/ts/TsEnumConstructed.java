package io.github.factoryfx.factory.typescript.generator.ts;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TsEnumConstructed extends TsFile {

    private final List<String> enumValues=new ArrayList<>();

    public TsEnumConstructed(String name, String subPath, Path targetPath) {
        super(name, subPath, targetPath);
    }

    @Override
    protected String getContent() {
        return
                    "export enum "+getName()+"  {\n" +
                    "\n" + enumValues.stream().map(enumValue->enumValue+"=\""+enumValue+"\"").collect(Collectors.joining(",\n"))+"\n" +
                    "\n" +
                    "}\n"+
                    "export namespace "+getName()+" {\n"+
                    "    export function fromJson(json: string): "+getName()+"{\n"+
                            "        return "+getName()+"[json];\n"+
                    "    }\n"+
                    "    export function toJson(value: "+getName()+"): string{\n"+
                            "        if (value) return value.toString();\n"+
                    "    }\n"+
                    "}\n";
    }

    public void addEnumValue(String value){
        enumValues.add(value);
    }

    public void addEnumValues(Set<String> values){
        enumValues.addAll(values);
    }

}
