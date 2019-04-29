package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.docu.rule.simulator.SimulatorBuilder;
import io.github.factoryfx.docu.rule.simulator.SimulatorRootFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.MicroserviceBuilder;
import io.github.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import io.github.factoryfx.factory.exception.ResettingHandler;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;

public class Main {

    public static void main(String[] args) {

        // this early in development, we are, unconditionally, starting up the simulator for the backend service...
        FactoryTreeBuilder<Server, SimulatorRootFactory, Void> simulatorBuilder = new SimulatorBuilder().builder();
        MicroserviceBuilder<Server, SimulatorRootFactory, Void> simBuilder = simulatorBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<Server, SimulatorRootFactory>())).
                withInMemoryStorage();
        Microservice<Server, SimulatorRootFactory, Void> simMicroservice = simBuilder.build();
        simMicroservice.start();

        // and now the Greetings service itself...
        FactoryTreeBuilder<Server, ServerRootFactory, Void> serverBuilder = new ServerBuilder().builder();
        MicroserviceBuilder<Server, ServerRootFactory, Void> builder = serverBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<Server, ServerRootFactory>())).
                withInMemoryStorage();
        Microservice<Server, ServerRootFactory, Void> microservice = builder.build();
        microservice.start();

    }
}
