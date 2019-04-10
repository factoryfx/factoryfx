package io.github.factoryfx.copperbridge;

import java.util.Collections;

import io.github.factoryfx.copperbridge.db.OracleDataSourceFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import org.copperengine.core.EngineIdProvider;
import org.copperengine.core.batcher.RetryingTxnBatchRunner;
import org.copperengine.core.batcher.impl.BatcherImpl;
import org.copperengine.core.common.DefaultProcessorPoolManager;
import org.copperengine.core.common.JdkRandomUUIDFactory;
import org.copperengine.core.common.ProcessorPoolManager;
import org.copperengine.core.persistent.PersistentPriorityProcessorPool;
import org.copperengine.core.persistent.PersistentProcessorPool;
import org.copperengine.core.persistent.PersistentScottyEngine;
import org.copperengine.core.persistent.ScottyDBStorage;
import org.copperengine.core.persistent.txn.CopperTransactionController;


public class PersistentScottyEngineFactory<R extends FactoryBase<?, R>> extends FactoryBase<PersistentEngineContainer, R> {

    public final StringAttribute idPrefix = new StringAttribute().labelText("processor pool id prefix");
    public final IntegerAttribute batcherThreads = new IntegerAttribute().labelText("Number of batcher threads");
    public final IntegerAttribute threads = new IntegerAttribute().labelText("Number of processing threads");


    public final FactoryAttribute<R,EngineIdProvider, EngineIdProviderFactory<R>> persistentEngineIdProviderFactory = new FactoryAttribute<>();
    public final FactoryAttribute<R,DBDialect, OracleDataSourceFactory<R>> dbDialectFactory = new FactoryAttribute<R,DBDialect, OracleDataSourceFactory<R>>().labelText("DB dialect");

    public PersistentScottyEngineFactory() {
        configLifeCycle().setCreator(this::createImpl);
        configLifeCycle().setStarter(PersistentEngineContainer::startup);
        configLifeCycle().setDestroyer(PersistentEngineContainer::shutdown);
    }

    public PersistentEngineContainer createImpl() {
        CopperTransactionController copperTransactionController = new CopperTransactionController(dbDialectFactory.instance().dataSource);

        PersistentPriorityProcessorPool processorPool = new PersistentPriorityProcessorPool(idPrefix.get(), copperTransactionController);
        processorPool.setNumberOfThreads(threads.get());
        ProcessorPoolManager<PersistentProcessorPool> processorPoolManager = new DefaultProcessorPoolManager<>();
        processorPoolManager.setProcessorPools(Collections.singletonList(processorPool));

        BatcherImpl batcher = new BatcherImpl(batcherThreads.get());
        batcher.setBatchRunner(new RetryingTxnBatchRunner(dbDialectFactory.instance().dataSource));

        ScottyDBStorage dbStorage = new ScottyDBStorage();
        dbStorage.setTransactionController(copperTransactionController);
        dbStorage.setBatcher(batcher);
        dbStorage.setCheckDbConsistencyAtStartup(true);

        dbStorage.setDialect(dbDialectFactory.instance().dOra);

        PersistentScottyEngine engine = new PersistentScottyEngine();
        engine.setIdFactory(new JdkRandomUUIDFactory());
        engine.setProcessorPoolManager(processorPoolManager);
        engine.setDbStorage(dbStorage);
        engine.setWfRepository(dbDialectFactory.instance().wfRepository);
        engine.setEngineIdProvider(persistentEngineIdProviderFactory.instance());
        return new PersistentEngineContainer(engine, batcher);
    }

}
