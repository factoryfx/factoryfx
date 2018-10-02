package de.factoryfx.copperbridge.test;

import java.util.List;

import de.factoryfx.copperbridge.BackchannelFactory;
import de.factoryfx.copperbridge.CopperEngineContextFactory;
import de.factoryfx.copperbridge.EngineIdProviderFactory;
import de.factoryfx.copperbridge.TransientScottyEngineFactory;
import de.factoryfx.copperbridge.WorkflowLauncher;
import de.factoryfx.copperbridge.WorkflowLauncherFactory;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.MicroserviceBuilder;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<CopperRootFactory> builder = new FactoryTreeBuilder<>(CopperRootFactory.class);

        builder.addFactory(TransientScottyEngineFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            TransientScottyEngineFactory transientScottyEngineFactory = new TransientScottyEngineFactory() {
                @Override
                public List<String> getWorkflowClassPaths() {
                    return List.of("de.factoryfx.copperbridge.test.wf");
                }
            };
            transientScottyEngineFactory.copperEngineContext.set(copperRootFactoryFactoryContext.get(CopperEngineContextFactoryImpl.class));
            transientScottyEngineFactory.threads.set(5);
            transientScottyEngineFactory.engineIdProviderFactory.set(copperRootFactoryFactoryContext.get(EngineIdProviderFactory.class));
            return transientScottyEngineFactory;
        });
        builder.addFactory(EngineIdProviderFactory.class, Scope.PROTOTYPE, copperRootFactoryFactoryContext -> {
            EngineIdProviderFactory engineIdProviderFactory = new EngineIdProviderFactory();
            engineIdProviderFactory.idPrefix.set("P#");
            return engineIdProviderFactory;
        });

        builder.addFactory(CopperEngineContextFactoryImpl.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            CopperEngineContextFactoryImpl copperEngineContextFactory = new CopperEngineContextFactoryImpl();
            copperEngineContextFactory.dep1.set("initial");
            copperEngineContextFactory.dependencyInjectorType.set("demoApp");
            return copperEngineContextFactory;
        });

        builder.addFactory(BackchannelFactory.class, Scope.SINGLETON);
        builder.addFactory(WorkflowLauncherFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            WorkflowLauncherFactory workflowLauncherFactory = new WorkflowLauncherFactory();
            workflowLauncherFactory.transientScottyEngine.set(copperRootFactoryFactoryContext.get(TransientScottyEngineFactory.class));
            workflowLauncherFactory.backchannel.set(copperRootFactoryFactoryContext.get(BackchannelFactory.class));
            return workflowLauncherFactory;
        });
        builder.addFactory(CopperRootFactory.class, Scope.SINGLETON);

        Microservice<Void, WorkflowLauncher, CopperRootFactory, String> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder.buildTree());
        microservice.start();

        new Thread(() -> {
            while (true) {
                microservice.getRootLiveObject().fire(WorkflowLauncher.EngineType.TRANSIENT, "TestWorkflow", "Hello", 1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataAndNewMetadata<CopperRootFactory> update = microservice.prepareNewFactory();
        CopperEngineContextFactory<Void, CopperRootFactory> da = update.root.workflowLauncher.get().transientScottyEngine.get().copperEngineContext.get();
        ((CopperEngineContextFactoryImpl) da).dep1.set("world");
        microservice.updateCurrentFactory(update, "admin", "update", a->true);


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        microservice.stop();
    }

}
