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
        FactoryTreeBuilder<CopperRootFactory> builder = buildApplication();

        Microservice<Void, WorkflowLauncher, CopperRootFactory, String> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder.buildTree());
        microservice.start();

        startWorkflowExecutingThread(microservice);

        sleep(500);
        {
            System.out.println("update1");
            DataAndNewMetadata<CopperRootFactory> update = microservice.prepareNewFactory();
            CopperEngineContextFactory<Void, CopperRootFactory> da = update.root.workflowLauncher.get().copperEngineContext.get();
            ((CopperEngineContextFactoryImpl) da).dep1.set("world");
            microservice.updateCurrentFactory(update, "admin", "update", a -> true);
            System.out.println("update1 fnished");
        }

        sleep(500);
        {
            System.out.println("update2");
            DataAndNewMetadata<CopperRootFactory> update = microservice.prepareNewFactory();
            TransientScottyEngineFactory<Void, CopperRootFactory> da = update.root.workflowLauncher.get().copperEngineContext.get().transientScottyEngine.get();
            da.threads.set(20);
            microservice.updateCurrentFactory(update, "admin", "update2", a -> true);
            System.out.println("update2 fnished");
        }

        sleep(500);

        microservice.stop();
    }

    private static void startWorkflowExecutingThread(Microservice<Void, WorkflowLauncher, CopperRootFactory, String> microservice) {
        new Thread(() -> {
            while (true) {
                microservice.getRootLiveObject().fire(WorkflowLauncher.EngineType.TRANSIENT, "TestWorkflow", "Hello", 1);
                sleep(100);
            }
        }).start();
    }


    @SuppressWarnings("unchecked")
    private static FactoryTreeBuilder<CopperRootFactory> buildApplication() {
        FactoryTreeBuilder<CopperRootFactory> builder = new FactoryTreeBuilder<>(CopperRootFactory.class);

        builder.addFactory(TransientScottyEngineFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            TransientScottyEngineFactory<Void,CopperRootFactory> transientScottyEngineFactory = new TransientScottyEngineFactory<>() {
                @Override
                public List<String> getWorkflowClassPaths() {
                    return List.of("de.factoryfx.copperbridge.test.wf");
                }
            };
            transientScottyEngineFactory.threads.set(5);
            transientScottyEngineFactory.engineIdProviderFactory.set(copperRootFactoryFactoryContext.get(EngineIdProviderFactory.class));
            return transientScottyEngineFactory;
        });
        builder.addFactory(EngineIdProviderFactory.class, Scope.PROTOTYPE, copperRootFactoryFactoryContext -> {
            EngineIdProviderFactory<Void,CopperRootFactory> engineIdProviderFactory = new EngineIdProviderFactory<>();
            engineIdProviderFactory.idPrefix.set("P#");
            return engineIdProviderFactory;
        });

        builder.addFactory(CopperEngineContextFactoryImpl.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            CopperEngineContextFactoryImpl copperEngineContextFactory = new CopperEngineContextFactoryImpl();
            copperEngineContextFactory.dep1.set("initial");
            copperEngineContextFactory.dependencyInjectorType.set("demoApp");
            copperEngineContextFactory.transientScottyEngine.set(copperRootFactoryFactoryContext.get(TransientScottyEngineFactory.class));
            return copperEngineContextFactory;
        });

        builder.addFactory(BackchannelFactory.class, Scope.SINGLETON);
        builder.addFactory(WorkflowLauncherFactory.class, Scope.SINGLETON, copperRootFactoryFactoryContext -> {
            WorkflowLauncherFactory<Void,CopperRootFactory> workflowLauncherFactory = new WorkflowLauncherFactory<>();
            workflowLauncherFactory.copperEngineContext.set(copperRootFactoryFactoryContext.get(CopperEngineContextFactoryImpl.class));
            workflowLauncherFactory.backchannel.set(copperRootFactoryFactoryContext.get(BackchannelFactory.class));
            return workflowLauncherFactory;
        });
        builder.addFactory(CopperRootFactory.class, Scope.SINGLETON);
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
