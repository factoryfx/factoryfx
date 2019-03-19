package io.github.factoryfx.copperbridge;

import java.util.Collections;
import java.util.List;

import io.github.factoryfx.data.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.EngineIdProvider;
import org.copperengine.core.common.DefaultProcessorPoolManager;
import org.copperengine.core.common.DefaultTicketPoolManager;
import org.copperengine.core.common.JdkRandomUUIDFactory;
import org.copperengine.core.common.ProcessorPoolManager;
import org.copperengine.core.monitoring.NullRuntimeStatisticsCollector;
import org.copperengine.core.tranzient.DefaultEarlyResponseContainer;
import org.copperengine.core.tranzient.DefaultTimeoutManager;
import org.copperengine.core.tranzient.TransientPriorityProcessorPool;
import org.copperengine.core.tranzient.TransientProcessorPool;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.ext.wfrepo.classpath.ClasspathWorkflowRepository;

import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public abstract class TransientScottyEngineFactory<R extends FactoryBase<?, R>> extends FactoryBase<TransientScottyEngine, R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<EngineIdProvider, EngineIdProviderFactory<R>> engineIdProviderFactory =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(EngineIdProviderFactory.class));

    public final IntegerAttribute threads = new IntegerAttribute().labelText("Number of processing threads");

    public abstract List<String> getWorkflowClassPaths();

    public TransientScottyEngineFactory() {
        configLifeCycle().setCreator(this::createImpl);
        configLifeCycle().setStarter(TransientScottyEngine::startup);
        configLifeCycle().setDestroyer(TransientScottyEngine::shutdown);
    }

    public TransientScottyEngine createImpl() {
        TransientScottyEngine engine = new TransientScottyEngine();
        TransientProcessorPool processorPool = new TransientPriorityProcessorPool(TransientProcessorPool.DEFAULT_POOL_ID, threads.get());
        ProcessorPoolManager<TransientProcessorPool> processorPoolManager = new DefaultProcessorPoolManager<>();
        processorPoolManager.setProcessorPools(Collections.singletonList(processorPool));

        engine.setEarlyResponseContainer(new DefaultEarlyResponseContainer());
        engine.setEngineIdProvider(engineIdProviderFactory.instance());
        engine.setIdFactory(new JdkRandomUUIDFactory());
        engine.setPoolManager(processorPoolManager);
        engine.setStatisticsCollector(new NullRuntimeStatisticsCollector());
        engine.setTicketPoolManager(new DefaultTicketPoolManager());
        engine.setTimeoutManager(new DefaultTimeoutManager());
        engine.setWfRepository(new ClasspathWorkflowRepository(getWorkflowClassPaths()));
        return engine;
    }
}
