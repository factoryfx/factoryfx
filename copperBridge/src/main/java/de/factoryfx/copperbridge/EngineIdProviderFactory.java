package de.factoryfx.copperbridge;

import org.copperengine.core.EngineIdProvider;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class EngineIdProviderFactory<V, R extends FactoryBase<?, V, R>> extends FactoryBase<EngineIdProvider, V, R> {
    public final StringAttribute idPrefix = new StringAttribute().labelText("id prefix");

    public EngineIdProviderFactory() {
        configLifeCycle().setCreator(() -> idPrefix::get);
    }
}
