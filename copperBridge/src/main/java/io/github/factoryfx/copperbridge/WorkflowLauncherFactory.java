package io.github.factoryfx.copperbridge;

import io.github.factoryfx.data.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.copperengine.core.util.Backchannel;

import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class WorkflowLauncherFactory<R extends FactoryBase<?, R>> extends SimpleFactoryBase<WorkflowLauncher, R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Backchannel, BackchannelFactory<R>> backchannel =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(BackchannelFactory.class)).labelText("Backchannel");

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<CopperEngineContext, CopperEngineContextFactory<R>> copperEngineContext =
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
