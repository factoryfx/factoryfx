package de.factoryfx.copperbridge.test;

import de.factoryfx.copperbridge.WorkflowLauncher;
import de.factoryfx.copperbridge.WorkflowLauncherFactory;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class CopperRootFactory extends FactoryBase<WorkflowLauncher, CopperRootFactory> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<WorkflowLauncher, WorkflowLauncherFactory<CopperRootFactory>> workflowLauncher =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(WorkflowLauncherFactory.class)).labelText("wfl");

    public CopperRootFactory(){
        configLifeCycle().setCreator(workflowLauncher::instance);
    }

}
