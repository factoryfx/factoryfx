package de.factoryfx.javascript.data.attributes.types;

import javafx.util.Pair;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.Collection;
import java.util.Map;

public class ScriptExecutor {

    private static final String NASHORN_SCRIPT_FILE_NAME = "javax.script.filename";

    private final NashornScriptEngine scriptEngine;
    private final SimpleBindings bindings;
    private final CompiledScript compiledScript;

    public ScriptExecutor(Collection<Pair<String, String>> libraries, String scriptName, String scriptCode, Map<String,Object> globalObjects) {
        scriptEngine = (NashornScriptEngine)new NashornScriptEngineFactory().getScriptEngine();
        bindings = new SimpleBindings();
        bindings.putAll(globalObjects);
        scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        libraries.forEach(p->{
            bindings.put(NASHORN_SCRIPT_FILE_NAME, p.getKey());
            try {
                scriptEngine.eval(p.getValue());
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            bindings.put(NASHORN_SCRIPT_FILE_NAME, scriptName);
            compiledScript = scriptEngine.compile(scriptCode);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public Object execute(Map<String,Object> variables) {
        bindings.putAll(variables);
        try {
            scriptEngine.getContext().setBindings(bindings,ScriptContext.ENGINE_SCOPE);
            return compiledScript.eval();
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        } finally {
            variables.keySet().forEach(bindings::remove);
        }
    }

}
