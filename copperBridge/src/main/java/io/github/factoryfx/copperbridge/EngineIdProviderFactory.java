package io.github.factoryfx.copperbridge;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.EngineIdProvider;

public class EngineIdProviderFactory<R extends FactoryBase<?, R>> extends FactoryBase<EngineIdProvider, R> {
    public final StringAttribute idPrefix = new StringAttribute().labelText("id prefix");

    public EngineIdProviderFactory() {
        configLifeCycle().setCreator(() -> idPrefix::get);
    }
}
