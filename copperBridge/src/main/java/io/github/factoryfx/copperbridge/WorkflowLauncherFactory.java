package io.github.factoryfx.copperbridge;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import org.copperengine.core.util.Backchannel;

import io.github.factoryfx.factory.util.LanguageText;

public class WorkflowLauncherFactory<R extends FactoryBase<?, R>> extends SimpleFactoryBase<WorkflowLauncher, R> {

    public final FactoryAttribute<R,Backchannel, BackchannelFactory<R>> backchannel = new FactoryAttribute<R,Backchannel, BackchannelFactory<R>>().labelText("Backchannel");
    public final FactoryAttribute<R,CopperEngineContext, CopperEngineContextFactory<R>> copperEngineContext = new FactoryAttribute<R,CopperEngineContext, CopperEngineContextFactory<R>>().labelText("Copper engine context");

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
