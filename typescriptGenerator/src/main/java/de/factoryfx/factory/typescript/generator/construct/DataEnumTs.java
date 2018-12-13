package de.factoryfx.factory.typescript.generator.construct;

import de.factoryfx.factory.typescript.generator.ts.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DataEnumTs {
    private final Class<? extends Enum> enumClass;
    private final Path targetPath;

    public DataEnumTs(Class<? extends Enum<?>> enumClass, Path targetPath) {
        this.enumClass = enumClass;
        this.targetPath = targetPath;
    }

    public TsEnumConstructed construct() {
        TsEnumConstructed constructed = new TsEnumConstructed(enumClass.getSimpleName(),enumClass.getPackage().getName().replace(".","/")+"/" ,targetPath);
        constructed.addEnumValues(getEnumNames());
        return constructed;
    }

    private Set<String> getEnumNames() {
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
    }
}
