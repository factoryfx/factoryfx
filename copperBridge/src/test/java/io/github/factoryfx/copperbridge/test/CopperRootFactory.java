package io.github.factoryfx.copperbridge.test;

import io.github.factoryfx.copperbridge.WorkflowLauncher;
import io.github.factoryfx.copperbridge.WorkflowLauncherFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class CopperRootFactory extends FactoryBase<WorkflowLauncher, CopperRootFactory> {

    public final FactoryAttribute<CopperRootFactory,WorkflowLauncher, WorkflowLauncherFactory<CopperRootFactory>> workflowLauncher = new FactoryAttribute<CopperRootFactory,WorkflowLauncher, WorkflowLauncherFactory<CopperRootFactory>>().labelText("wfl");

    public CopperRootFactory(){
        configLifeCycle().setCreator(workflowLauncher::instance);
    }

}
