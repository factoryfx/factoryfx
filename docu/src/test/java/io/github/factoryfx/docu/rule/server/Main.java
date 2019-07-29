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
        FactoryTreeBuilder<Server, SimulatorRootFactory> simulatorBuilder = new SimulatorBuilder().builder();
        MicroserviceBuilder<Server, SimulatorRootFactory> simBuilder = simulatorBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<Server, SimulatorRootFactory>()));
        Microservice<Server, SimulatorRootFactory> simMicroservice = simBuilder.build();
        simMicroservice.start();

        // and now the Greetings service itself...
        FactoryTreeBuilder<Server, ServerRootFactory> serverBuilder = new ServerBuilder().builder();
        MicroserviceBuilder<Server, ServerRootFactory> builder = serverBuilder.microservice().
                withExceptionHandler(new LoggingFactoryExceptionHandler<>(new ResettingHandler<Server, ServerRootFactory>()));
        Microservice<Server, ServerRootFactory> microservice = builder.build();
        microservice.start();

    }
}
