package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.ShopJettyServerFactory;
import io.github.factoryfx.example.server.testutils.FactoryTreeBuilderRule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * This test seems to do more than necessary, by actually starting a real server where we could have just
 * called the resource class directly (as in ShopResourceTest).
 *
 * This is on purpose and would require a more complicated example to show its usefulness:
 * Consider an application that requires a backend service for its work. In situations where
 * the real backend service is not available, we could either use a simple mock (which will
 * only be useful in unit tests), but we might instead want to use a simulator which behaves
 * more or less the same as the real thing.
 *
 * Such a simulator is what we're, well, simulating in this unit test, as we're starting up
 * a Jetty server instance on an ephemeral port.
 *
 * Actually, in a real world unit test using such a simulator, we might have two separate
 * FactoryTreeBuilderRules, one for the simulator and one for the application which uses
 * the simulator by data injection (full url, but especially the port number).
 *
 * Here, we just write a simple dummy consumer of our webservice instead.
 *
 * @see <a href="https://github.com/factoryfx/factoryfx/tree/master/docu/src/test/java/io/github/factoryfx/docu/rule">https://github.com/factoryfx/factoryfx/tree/master/docu/src/test/java/io/github/factoryfx/docu/rule</a>
 */
public class ServerTest {

    public Server server;

    @RegisterExtension
    public final FactoryTreeBuilderRule<Server, ServerRootFactory, Void> ctx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder(), rule -> {

        rule.getFactory(ShopJettyServerFactory.class).connectors.get(0).port.set(0);

        server = rule.get(ShopJettyServerFactory.class);
    });

    @Test
    public void testPort() {

        int simPort = ((ServerConnector)(server.getConnectors()[0])).getLocalPort();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:" + simPort + "/shop/products");

        // just for demonstration we won't really decode the JSON stuff and just look at the JSON string itself
        String resp = webTarget.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);

        // "Car" is one of the products in the shop. We *know* that, but this is for demonstration purposes only.
        MatcherAssert.assertThat("must contain 'Car'", resp, Matchers.containsString("Car"));
    }
}
