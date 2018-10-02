package de.factoryfx.copperbridge;

import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.core.util.Backchannel;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class WorkflowLauncherFactory<V, R extends FactoryBase<?, V, R>> extends SimpleFactoryBase<WorkflowLauncher, V, R> {

    public final FactoryReferenceAttribute<TransientScottyEngine, TransientScottyEngineFactory<V, R>> transientScottyEngine =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(TransientScottyEngineFactory.class)).labelText("Transient engine").nullable();

    public final FactoryReferenceAttribute<PersistentEngineContainer, PersistentScottyEngineFactory<V, R>> persistentScottyEngine =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(PersistentScottyEngineFactory.class)).labelText("Persistent engine").nullable();

    public final FactoryReferenceAttribute<Backchannel, BackchannelFactory<V, R>> backchannel =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(BackchannelFactory.class)).labelText("Backchannel");

    public WorkflowLauncherFactory(){
        config().addValidation(a-> new ValidationResult(transientScottyEngine.get() == null && persistentScottyEngine.get()==null, new LanguageText().en("Transient and persistent engine must not both be null")), transientScottyEngine, persistentScottyEngine);
    }

    @Override
    public WorkflowLauncher createImpl() {
        return new WorkflowLauncher(transientScottyEngine.instance(),
                                    persistentScottyEngine.instance()== null ? null : persistentScottyEngine.instance().persistentProcessingEngine,
                                    backchannel.instance());
    }
}
