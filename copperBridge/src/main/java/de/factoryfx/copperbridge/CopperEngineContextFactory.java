package de.factoryfx.copperbridge;

import java.util.Map;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public abstract class CopperEngineContextFactory<V, R extends FactoryBase<?, V, R>> extends SimpleFactoryBase<CopperEngineContext, V, R> {

    public final StringAttribute dependencyInjectorType = new StringAttribute().labelText("DependencyInjectorType");

    @Override
    public CopperEngineContext createImpl() {
        return new CopperEngineContext(dependencyInjectorType.get(), createDependencyHashMap());
    }

    public abstract Map<String, Object> createDependencyHashMap();
}
