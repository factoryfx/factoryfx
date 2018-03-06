package de.factoryfx.process;

import de.factoryfx.factory.builder.FactoryContext;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

import java.util.function.Function;

public class SubscriptionTestMain {

    public static void main(String[] args) {
        FactoryTreeBuilder<Void,SubscriptionInitializer,SubscriptionInitializerFactory> builder= new FactoryTreeBuilder<>(SubscriptionInitializerFactory.class);
        builder.addFactory(SubscriptionInitializerFactory.class, Scope.SINGLETON, new Function<FactoryContext<Void>, SubscriptionInitializerFactory>() {
            @Override
            public SubscriptionInitializerFactory apply(FactoryContext<Void> context) {
                SubscriptionInitializerFactory factoryBases = new SubscriptionInitializerFactory();
                factoryBases.processExecutor.set(context.get(ProcessExecutorFactory.class));
                return factoryBases;
            }
        });

        builder.addFactory(ProcessExecutorFactory.class, Scope.SINGLETON, new Function<FactoryContext<Void>, ProcessExecutorFactory>() {
            @Override
            public ProcessExecutorFactory apply(FactoryContext<Void> context) {
                ProcessExecutorFactory processExecutorFactory = new ProcessExecutorFactory();
                processExecutorFactory.processStorage.set(context.get(ProcessExecutorFactory.class));
                return processExecutorFactory;
            }
        });



        builder.buildTree();
    }



}
