package de.factoryfx.process;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

public class SubscriptionTestMain {

    public static void main(String[] args) {
        FactoryTreeBuilder<SubscriptionInitializerFactory> builder= new FactoryTreeBuilder<>(SubscriptionInitializerFactory.class);
        builder.addFactory(SubscriptionInitializerFactory.class, Scope.SINGLETON, context -> {
            SubscriptionInitializerFactory factoryBases = new SubscriptionInitializerFactory();
            factoryBases.processExecutor.set(context.get(ProcessExecutorFactory.class));
            return factoryBases;
        });

        builder.addFactory(ProcessExecutorFactory.class, Scope.SINGLETON, context -> {
            ProcessExecutorFactory processExecutorFactory = new ProcessExecutorFactory();
            processExecutorFactory.processStorage.set(context.get(ProcessExecutorFactory.class));
            return processExecutorFactory;
        });



        builder.buildTree();
    }



}
