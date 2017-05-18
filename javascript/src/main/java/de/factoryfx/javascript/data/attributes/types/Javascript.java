package de.factoryfx.javascript.data.attributes.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import javafx.util.Pair;

public class Javascript<A> {

    private final String code;

    @JsonIgnore
    private final String headerCode;

    @JsonIgnore
    private final String declarationCode;

    @JsonIgnore
    private final ScriptExecutorCache scriptExecutorCache = new ScriptExecutorCache();

    @JsonCreator
    public Javascript(String code) {
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
        return Objects.equals(code, o.code) && Objects.equals(headerCode, o.headerCode);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public void execute(A api) {
        final ScriptExecutor scriptExecutor = scriptExecutorCache.take();
        Map<String,Object> map = new HashMap<>();
        map.put("api", api);
        scriptExecutor.execute(map);
        scriptExecutorCache.putBack(scriptExecutor);
    }

    public Javascript copyWithNewCode(String newCode) {
        Javascript copy = new Javascript(newCode,headerCode,declarationCode);
        return copy;
    }


    public String getHeaderCode() {
        return headerCode;
    }

    public String getDeclarationCode() {
        return declarationCode;
    }

    /** optimisation for multithreading */
    final class ScriptExecutorCache {
        LinkedBlockingDeque<ScriptExecutor> executors = new LinkedBlockingDeque<>();

        public ScriptExecutor take(){
            final ScriptExecutor executor = executors.pollFirst();
            if (executor==null){
                return createExecutor();
            }
            return executor;
        }

        public void putBack(ScriptExecutor scriptExecutor){
            executors.push(scriptExecutor);
        }


        private ScriptExecutor createExecutor() {
            return new ScriptExecutor(Collections.singletonList(new Pair<>("header", headerCode)), "rule", code, Collections.emptyMap());
        }


    }

}
