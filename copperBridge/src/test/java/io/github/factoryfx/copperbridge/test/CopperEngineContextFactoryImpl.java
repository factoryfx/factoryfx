package io.github.factoryfx.copperbridge.test;

import java.util.Map;

import io.github.factoryfx.copperbridge.CopperEngineContextFactory;
import io.github.factoryfx.data.attribute.types.StringAttribute;

public class CopperEngineContextFactoryImpl extends CopperEngineContextFactory<CopperRootFactory> {

    public final StringAttribute dep1 = new StringAttribute().labelText("dependency 1");

    public static final String DEPENDENCY = "dep1";

    @Override
    public Map<String, Object> createDependencyHashMap() {
        return Map.of(DEPENDENCY, dep1.get());
    }
}
