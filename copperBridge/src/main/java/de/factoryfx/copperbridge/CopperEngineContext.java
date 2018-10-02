package de.factoryfx.copperbridge;

import java.util.Map;

import org.copperengine.core.AbstractDependencyInjector;

public class CopperEngineContext extends AbstractDependencyInjector {

    private final String type;
    private Map<String, Object> dependencyMap;

    public CopperEngineContext(String type, Map<String, Object> dependencyMap) {
        this.type = type;
        this.dependencyMap = dependencyMap;
    }

    @Override
    protected Object getBean(String beanId) {
        return dependencyMap.get(beanId);
    }

    @Override
    public String getType() {
        return type;
    }
}
