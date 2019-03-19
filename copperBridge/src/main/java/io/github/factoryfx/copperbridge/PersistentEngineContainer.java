package io.github.factoryfx.copperbridge;

import org.copperengine.core.batcher.impl.BatcherImpl;
import org.copperengine.core.persistent.PersistentScottyEngine;

class PersistentEngineContainer {

    final PersistentScottyEngine persistentProcessingEngine;
    private final BatcherImpl batcher;

    PersistentEngineContainer(PersistentScottyEngine persistentProcessingEngine, BatcherImpl batcher) {
        this.persistentProcessingEngine = persistentProcessingEngine;
        this.batcher = batcher;
    }

    void startup() {
        batcher.startup();
        persistentProcessingEngine.startup();
    }

    void shutdown() {
        persistentProcessingEngine.shutdown();
        batcher.shutdown();
    }

}
