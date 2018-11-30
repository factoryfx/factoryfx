package de.factoryfx.copperbridge;

import java.util.Map;

import org.copperengine.core.tranzient.TransientScottyEngine;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public abstract class CopperEngineContextFactory<V, R extends FactoryBase<?, V, R>> extends FactoryBase<CopperEngineContext, V, R> {

    public final StringAttribute dependencyInjectorType = new StringAttribute().labelText("DependencyInjectorType");

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<TransientScottyEngine, TransientScottyEngineFactory<V, R>> transientScottyEngine =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(TransientScottyEngineFactory.class)).labelText("Transient engine").nullable();

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<PersistentEngineContainer, PersistentScottyEngineFactory<V, R>> persistentScottyEngine =
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
