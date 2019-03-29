package io.github.factoryfx.copperbridge;

import java.util.Map;

import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.tranzient.TransientScottyEngine;


public abstract class CopperEngineContextFactory<R extends FactoryBase<?, R>> extends FactoryBase<CopperEngineContext, R> {

    public final StringAttribute dependencyInjectorType = new StringAttribute().labelText("DependencyInjectorType");

    public final FactoryReferenceAttribute<R,TransientScottyEngine, TransientScottyEngineFactory<R>> transientScottyEngine = new FactoryReferenceAttribute<R,TransientScottyEngine, TransientScottyEngineFactory<R>>().labelText("Transient engine").nullable();
    public final FactoryReferenceAttribute<R,PersistentEngineContainer, PersistentScottyEngineFactory<R>> persistentScottyEngine = new FactoryReferenceAttribute<R,PersistentEngineContainer, PersistentScottyEngineFactory<R>>().labelText("Persistent engine").nullable();

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
