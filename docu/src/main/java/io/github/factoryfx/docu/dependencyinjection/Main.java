package io.github.factoryfx.docu.dependencyinjection;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx-> {
            RootFactory rootFactory = new RootFactory();
            rootFactory.dependency.set(new DependencyFactory());
            return rootFactory;
        });

        Microservice<Root,RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

    }
}
