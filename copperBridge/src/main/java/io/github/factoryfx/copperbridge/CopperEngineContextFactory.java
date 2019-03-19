package io.github.factoryfx.copperbridge;

import java.util.Map;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.tranzient.TransientScottyEngine;

import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public abstract class CopperEngineContextFactory<R extends FactoryBase<?, R>> extends FactoryBase<CopperEngineContext, R> {

    public final StringAttribute dependencyInjectorType = new StringAttribute().labelText("DependencyInjectorType");

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<TransientScottyEngine, TransientScottyEngineFactory<R>> transientScottyEngine =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(TransientScottyEngineFactory.class)).labelText("Transient engine").nullable();

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<PersistentEngineContainer, PersistentScottyEngineFactory<R>> persistentScottyEngine =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(PersistentScottyEngineFactory.class)).labelText("Persistent engine").nullable();

    public CopperEngineContextFactory(){
        configLifeCycle().setCreator(()->{
            // Here we need to break the ffx dependency injection, because we don't need a new engine in case of a dependency change
            CopperEngineContext dependencyInjector = new CopperEngineContext(dependencyInjectorType.get(), createDependencyHashMap());
            if(transientScottyEngine.instance()!=null) transientScottyEngine.instance().setDependencyInjector(dependencyInjector);
            if(persistentScottyEngine.instance()!=null) persistentScottyEngine.instance().persistentProcessingEngine.setDependencyInjector(dependencyInjector);
            return dependencyInjector;
        });
    }

    public abstract Map<String, Object> createDependencyHashMap();
}
