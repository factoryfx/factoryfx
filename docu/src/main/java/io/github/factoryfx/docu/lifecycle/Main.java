package io.github.factoryfx.docu.lifecycle;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx-> new RootFactory());
        Microservice<Root,RootFactory> microservice = builder.microservice().build();

        microservice.start();
        microservice.stop();

    }
}
