package de.factoryfx.copperbridge;

import org.copperengine.core.util.Backchannel;

import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class WorkflowLauncherFactory<V, R extends FactoryBase<?, V, R>> extends SimpleFactoryBase<WorkflowLauncher, V, R> {

    public final FactoryReferenceAttribute<Backchannel, BackchannelFactory<V, R>> backchannel =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(BackchannelFactory.class)).labelText("Backchannel");

    public final FactoryReferenceAttribute<CopperEngineContext, CopperEngineContextFactory<V, R>> copperEngineContext =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(CopperEngineContextFactory.class).labelText("Copper engine context"));


    public WorkflowLauncherFactory(){
        config().addValidation(a-> new ValidationResult(copperEngineContext.get() == null
                                                            || (copperEngineContext.get().transientScottyEngine.get() == null && copperEngineContext.get().persistentScottyEngine.get() == null),
                                                        new LanguageText().en("Transient and persistent engine must not both be null")), copperEngineContext);
    }

    @Override
    public WorkflowLauncher createImpl() {
        return new WorkflowLauncher(copperEngineContext.get().transientScottyEngine.instance(),
                                    copperEngineContext.get().persistentScottyEngine.instance()== null ? null : copperEngineContext.get().persistentScottyEngine.instance().persistentProcessingEngine,
                                    backchannel.instance());
    }
}
