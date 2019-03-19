package io.github.factoryfx.copperbridge.test.wf;

import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.WorkflowDescription;
import org.copperengine.core.persistent.PersistentWorkflow;

import io.github.factoryfx.copperbridge.test.CopperEngineContextFactoryImpl;

@WorkflowDescription(alias = "TestWorkflow", majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class TestWorkflow extends PersistentWorkflow<String> {

    private String dependency;

    @Override
    public void main() throws Interrupt {
        System.out.println(getData() + " " + dependency);
    }

    @AutoWire(beanId = CopperEngineContextFactoryImpl.DEPENDENCY)
    public void setDependency(String dependency) {
        this.dependency = dependency;
    }
}
