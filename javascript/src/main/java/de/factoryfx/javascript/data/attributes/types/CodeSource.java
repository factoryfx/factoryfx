package de.factoryfx.javascript.data.attributes.types;

import java.util.Optional;

public class CodeSource {

    public final String sourceName;
    public final String code;

    public CodeSource(String sourceName, String code) {
        this.sourceName = Optional.ofNullable(sourceName).filter(s->!s.isEmpty()).orElse("<unknown>");
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeSource that = (CodeSource) o;

        if (!sourceName.equals(that.sourceName)) return false;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        int result = sourceName.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }
}
