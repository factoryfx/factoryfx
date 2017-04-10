package de.factoryfx.javascript.data.attributes.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

public class Javascript<A> {

    private final String code;

    @JsonIgnore
    private final String headerCode;

    @JsonIgnore
    private final String declarationCode;

    @JsonIgnore
    private final PrimitivePool primitivePool = new PrimitivePool();

    @JsonCreator
    public Javascript(@JsonProperty("code") String code) {
        this(code,"","");
    }

    public Javascript() {
        this("","","");
    }

    public Javascript(String code, String headerCode, String declarationCode) {
        this.code = code;
        this.headerCode = headerCode;
        this.declarationCode = declarationCode;
    }

    public boolean match(Javascript<A> o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        return Objects.equals(code, o.code)
                && Objects.equals(headerCode, o.headerCode);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public Javascript copy() {
        return new Javascript(code);
    }

    public void execute(A api) {
        primitivePool.withExecutor(e->{
            Map<String,Object> map = new HashMap<>();
            map.put("api", api);
            e.execute(map);
            return null;
        });
    }


    public Javascript copyWithNewHeaderCode(String headerCode) {
        Javascript copy = new Javascript(code,headerCode,declarationCode);
        return copy;
    }

    public Javascript copyWithNewCode(String newCode) {
        Javascript copy = new Javascript(newCode,headerCode,declarationCode);
        return copy;
    }

    public Javascript copyWithNewDeclarationCode(String declarationCode) {
        Javascript copy = new Javascript(code,headerCode,declarationCode);
        return copy;
    }


    public String getHeaderCode() {
        return headerCode;
    }

    final class PrimitivePool {

        LinkedBlockingDeque<ScriptExecutor> executors = new LinkedBlockingDeque<>();

        public Object withExecutor(Function<ScriptExecutor,Object> executor) {
            ScriptExecutor ex = Optional.ofNullable(executors.pollFirst()).orElseGet(()->createExecutor());
            try {
                return executor.apply(ex);
            } finally {
                executors.push(ex);
            }
        }

        private ScriptExecutor createExecutor() {
            return new ScriptExecutor(Arrays.asList(new Pair<>("header",headerCode)), "rule", code, Collections.emptyMap());
        }


    }

}
