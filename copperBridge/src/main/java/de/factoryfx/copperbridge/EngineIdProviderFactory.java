package de.factoryfx.copperbridge;

import org.copperengine.core.EngineIdProvider;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class EngineIdProviderFactory<R extends FactoryBase<?, R>> extends FactoryBase<EngineIdProvider, R> {
    public final StringAttribute idPrefix = new StringAttribute().labelText("id prefix");

    public EngineIdProviderFactory() {
        configLifeCycle().setCreator(() -> idPrefix::get);
    }
}
