package io.github.factoryfx.docu.rule.tests;

import io.github.factoryfx.docu.rule.server.*;
import io.github.factoryfx.docu.rule.simulator.SimulatorBuilder;
import io.github.factoryfx.docu.rule.simulator.SimulatorRootFactory;
import io.github.factoryfx.jetty.HttpServerConnectorFactory;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class GreetingsResourceTest {

    private static Server simulator;
    private static GreetingsResource greetingsResource;

    @SuppressWarnings("unchecked")
    @RegisterExtension
    @Order(1)
    public static final FactoryTreeBuilderRule<Server, SimulatorRootFactory> simulatorCtx = new FactoryTreeBuilderRule<>(new SimulatorBuilder().builder(), rule -> {

        HttpServerConnectorFactory<SimulatorRootFactory> factoryBase = (HttpServerConnectorFactory<SimulatorRootFactory>) rule.getFactory(JettyServerFactory.class).connectors.get(0);
        factoryBase.port.set(0);

        simulator = rule.get(JettyServerFactory.class);
    });

    @RegisterExtension
    @Order(2)
    public static final FactoryTreeBuilderRule<Server, ServerRootFactory> serverCtx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder(), rule -> {

        int simPort = ((ServerConnector)(simulator.getConnectors()[0])).getLocalPort();
        rule.getFactory(BackendClientFactory.class).backendPort.set(simPort);

        greetingsResource = rule.get(GreetingsResourceFactory.class);
    });

    @Test
    public void testGreeting() {
        Assertions.assertEquals("hello world",greetingsResource.greet());
    }

    @Test
    public void testGreetingOnceMore() {
        Assertions.assertEquals("hello world",greetingsResource.greet());
    }
}
