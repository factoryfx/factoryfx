package io.github.factoryfx.copperbridge.test;

import io.github.factoryfx.copperbridge.WorkflowLauncher;
import io.github.factoryfx.copperbridge.WorkflowLauncherFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class CopperRootFactory extends FactoryBase<WorkflowLauncher, CopperRootFactory> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<WorkflowLauncher, WorkflowLauncherFactory<CopperRootFactory>> workflowLauncher =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(WorkflowLauncherFactory.class)).labelText("wfl");

    public CopperRootFactory(){
        configLifeCycle().setCreator(workflowLauncher::instance);
    }

}
