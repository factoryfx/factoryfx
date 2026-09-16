package io.github.factoryfx.docu.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

/**
 * a typed remote reference: injectable like any factory, but the live object is a jersey proxy
 * of the shared api interface (cloned from the MicroserviceRestClientFactory pattern).
 * host/port/expectedContractHash are written by the control plane when it deploys the system.
 *
 * unlike a local live object reference the proxy is lazy: creation succeeds with the remote down,
 * each invocation may throw a jakarta.ws.rs.ProcessingException/WebApplicationException.
 */
public class GreetingApiClientFactory extends SimpleFactoryBase<GreetingApi, JettyServerRootFactory> {
    public final StringAttribute host = new StringAttribute().labelText("host").nullable();
    public final IntegerAttribute port = new IntegerAttribute().labelText("port").nullable();
    public final StringAttribute expectedContractHash = new StringAttribute().labelText("expectedContractHash").nullable();
    public final IntegerAttribute connectTimeoutMs = new IntegerAttribute().labelText("connectTimeoutMs").defaultValue(5000);
    public final IntegerAttribute readTimeoutMs = new IntegerAttribute().labelText("readTimeoutMs").defaultValue(5000);

    @Override
    protected GreetingApi createImpl() {
        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutMs.get());
        configuration.property(ClientProperties.READ_TIMEOUT, readTimeoutMs.get());
        Client client = ClientBuilder.newClient(configuration);
        return WebResourceFactory.newResource(GreetingApi.class, client.target("http://" + host.get() + ":" + port.get() + "/"));
    }
}
