package io.github.factoryfx.docu.rule.tests;

import io.github.factoryfx.docu.rule.server.*;
import io.github.factoryfx.docu.rule.simulator.HelloJettyServerFactory;
import io.github.factoryfx.docu.rule.simulator.SimulatorBuilder;
import io.github.factoryfx.docu.rule.simulator.SimulatorRootFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GreetingsResourceTest {

    private Server simulator;
    private GreetingsResource greetingsResource;

    @RegisterExtension
    @Order(1)
    public final FactoryTreeBuilderRule<Server, SimulatorRootFactory> simulatorCtx = new FactoryTreeBuilderRule<>(new SimulatorBuilder().builder(), rule -> {

        rule.getFactory(HelloJettyServerFactory.class).connectors.get(0).port.set(0);

        simulator = rule.get(HelloJettyServerFactory.class);
    });

    @RegisterExtension
    @Order(2)
    public final FactoryTreeBuilderRule<Server, ServerRootFactory> serverCtx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder(), rule -> {

        int simPort = ((ServerConnector)(simulator.getConnectors()[0])).getLocalPort();
        rule.getFactory(BackendClientFactory.class).backendPort.set(simPort);

        greetingsResource = rule.get(GreetingsResourceFactory.class);
    });

    @Test
    public void testGreeting() {
        assertThat(greetingsResource.greet(), is("hello world"));
    }

    @Test
    public void testGreetingOnceMore() {
        assertThat(greetingsResource.greet(), is("hello world"));
    }
}
