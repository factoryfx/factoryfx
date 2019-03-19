package io.github.factoryfx.copperbridge;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.copperengine.core.Acknowledge;
import org.copperengine.core.CopperException;
import org.copperengine.core.Response;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.common.AbstractProcessingEngine;
import org.copperengine.core.persistent.PersistentScottyEngine;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.core.util.Backchannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowLauncher {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowLauncher.class);

    private final TransientScottyEngine transientEngine;
    private final PersistentScottyEngine persistentEngine;
    private final Backchannel backchannel;

    public WorkflowLauncher(TransientScottyEngine transientEngine, PersistentScottyEngine persistentEngine, Backchannel backchannel) {
        this.transientEngine = transientEngine;
        this.persistentEngine = persistentEngine;
        this.backchannel = backchannel;
    }

    public enum EngineType{
        PERSISTENT,
        TRANSIENT;
        private AbstractProcessingEngine getEngine(TransientScottyEngine transientScottyEngine, PersistentScottyEngine persistentScottyEngine){
            return this == PERSISTENT? persistentScottyEngine : transientScottyEngine;
        }
    }

    public void fire(EngineType engineType, String workflowName, Object inputBean, int priority) {
        try {
            logger.debug("CALL -- {}({})", workflowName, inputBean);
            WorkflowInstanceDescr<Object> wid = createWID(workflowName, inputBean, priority);
            engineType.getEngine(transientEngine, persistentEngine).run(wid);
        } catch (CopperException e) {
            logger.error("Error in workflow", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <OUT> OUT get(EngineType engineType, String workflowName, Object inputBean, int priority) {
        try {
            logger.debug("CALL -- {}({})", workflowName, inputBean);
            WorkflowInstanceDescr<Object> wid = createWID(workflowName, inputBean, priority);

            engineType.getEngine(transientEngine, persistentEngine).run(wid);

            Object response = backchannel.wait(wid.getId(), 900, TimeUnit.DAYS);

            logger.debug("RETURN -- {}(..)={}", workflowName, response);
            return (OUT) response;
        } catch (CopperException | InterruptedException e) {
            logger.error("Error in workflow", e);
            return null;
        }
    }

    public void notifyPersistentEngine(String correlationId) {
        persistentEngine.notify(new Response<>(correlationId), new Acknowledge.DefaultAcknowledge());
    }

    public static WorkflowInstanceDescr<Object> createWID(String workflowName, Object inputBean, int priority) {
        WorkflowInstanceDescr<Object> wid = new WorkflowInstanceDescr<>(workflowName);
        wid.setData(inputBean);
        wid.setId(UUID.randomUUID().toString());
        wid.setPriority(priority);
        return wid;
    }

}
