package io.github.factoryfx.microservice.test.systemtree;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

/**
 * spike prototype of RemoteReferenceFactory&lt;Api&gt;: a typed remote reference injectable like any factory,
 * cloned from the MicroserviceRestClientFactory proxy pattern. host/port/expectedContractHash are set by the
 * control plane when it compiles a cross-service wiring entry.
 *
 * unlike a local liveObject reference the proxy is lazy: creation succeeds with the remote down,
 * each invocation may throw a jakarta.ws.rs.ProcessingException/WebApplicationException.
 */
public class TimeApiClientFactory<R extends FactoryBase<?, R>> extends SimpleFactoryBase<TimeApi, R> {
    public final StringAttribute host = new StringAttribute().labelText("host").nullable();
    public final IntegerAttribute port = new IntegerAttribute().labelText("port");
    public final StringAttribute expectedContractHash = new StringAttribute().labelText("expectedContractHash").nullable();
    public final IntegerAttribute connectTimeoutMs = new IntegerAttribute().labelText("connectTimeoutMs").defaultValue(5000);
    public final IntegerAttribute readTimeoutMs = new IntegerAttribute().labelText("readTimeoutMs").defaultValue(5000);

    @Override
    protected TimeApi createImpl() {
        ClientConfig configuration = new ClientConfig();
        configuration.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutMs.get());
        configuration.property(ClientProperties.READ_TIMEOUT, readTimeoutMs.get());
        Client client = ClientBuilder.newClient(configuration);
        return WebResourceFactory.newResource(TimeApi.class, client.target("http://" + host.get() + ":" + port.get() + "/"));
    }
}
