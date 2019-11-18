package io.github.factoryfx.copperbridge.test;

import java.util.List;

import io.github.factoryfx.copperbridge.BackchannelFactory;
import io.github.factoryfx.copperbridge.CopperEngineContextFactory;
import io.github.factoryfx.copperbridge.EngineIdProviderFactory;
import io.github.factoryfx.copperbridge.TransientScottyEngineFactory;
import io.github.factoryfx.copperbridge.WorkflowLauncher;
import io.github.factoryfx.copperbridge.WorkflowLauncherFactory;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder< WorkflowLauncher, CopperRootFactory> builder = buildApplication();

        Microservice<WorkflowLauncher, CopperRootFactory> microservice = builder.microservice().build();
        microservice.start();

        startWorkflowExecutingThread(microservice);

        sleep(500);
        {
            System.out.println("update1");
            DataUpdate<CopperRootFactory> update = microservice.prepareNewFactory();
            CopperEngineContextFactory<CopperRootFactory> da = update.root.workflowLauncher.get().copperEngineContext.get();
            ((CopperEngineContextFactoryImpl) da).dep1.set("world");
            microservice.updateCurrentFactory(update);
            System.out.println("update1 fnished");
        }

        sleep(500);
        {
            System.out.println("update2");
            DataUpdate<CopperRootFactory> update = microservice.prepareNewFactory();
            TransientScottyEngineFactory<CopperRootFactory> da = update.root.workflowLauncher.get().copperEngineContext.get().transientScottyEngine.get();
            da.threads.set(20);
            microservice.updateCurrentFactory(update);
            System.out.println("update2 fnished");
        }

        sleep(500);

        microservice.stop();
    }

    private static void startWorkflowExecutingThread(Microservice<WorkflowLauncher, CopperRootFactory> microservice) {
        new Thread(() -> {
            while (true) {
                microservice.getRootLiveObject().fire(WorkflowLauncher.EngineType.TRANSIENT, "TestWorkflow", "Hello", 1);
                sleep(100);
            }
        }).start();
    }



    private static FactoryTreeBuilder< WorkflowLauncher, CopperRootFactory> buildApplication() {
        FactoryTreeBuilder< WorkflowLauncher, CopperRootFactory> builder = new FactoryTreeBuilder<>(CopperRootFactory.class);

        builder.addFactoryUnsafe(TransientScottyEngineFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            TransientScottyEngineFactory<CopperRootFactory> transientScottyEngineFactory = new TransientScottyEngineFactory<>() {
                @Override
                public List<String> getWorkflowClassPaths() {
                    return List.of("io.github.factoryfx.copperbridge.test.wf");
                }
            };
            transientScottyEngineFactory.threads.set(5);
            transientScottyEngineFactory.engineIdProviderFactory.set(copperRootFactoryFactoryContext.getUnsafe(EngineIdProviderFactory.class));
            return transientScottyEngineFactory;
        });
        builder.addFactoryUnsafe(EngineIdProviderFactory.class, Scope.PROTOTYPE, copperRootFactoryFactoryContext -> {
            EngineIdProviderFactory<CopperRootFactory> engineIdProviderFactory = new EngineIdProviderFactory<>();
            engineIdProviderFactory.idPrefix.set("P#");
            return engineIdProviderFactory;
        });

        builder.addSingleton(CopperEngineContextFactoryImpl.class, copperRootFactoryFactoryContext -> {
            CopperEngineContextFactoryImpl copperEngineContextFactory = new CopperEngineContextFactoryImpl();
            copperEngineContextFactory.dep1.set("initial");
            copperEngineContextFactory.dependencyInjectorType.set("demoApp");
            copperEngineContextFactory.transientScottyEngine.set(copperRootFactoryFactoryContext.getUnsafe(TransientScottyEngineFactory.class));
            return copperEngineContextFactory;
        });

        builder.addFactoryUnsafe(BackchannelFactory.class, Scope.SINGLETON);
        builder.addFactoryUnsafe(WorkflowLauncherFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            WorkflowLauncherFactory<CopperRootFactory> workflowLauncherFactory = new WorkflowLauncherFactory<>();
            workflowLauncherFactory.copperEngineContext.set(copperRootFactoryFactoryContext.get(CopperEngineContextFactoryImpl.class));
            workflowLauncherFactory.backchannel.set(copperRootFactoryFactoryContext.getUnsafe(BackchannelFactory.class));
            return workflowLauncherFactory;
        });
        return builder;
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
