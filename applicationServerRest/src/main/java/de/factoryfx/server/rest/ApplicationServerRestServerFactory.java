package de.factoryfx.server.rest;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.URIListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.server.ApplicationServer;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ApplicationServerRestServerFactory<L,V,T extends FactoryBase<L,V>> extends FactoryBase<ApplicationServerRestServer,Void> {

    public final URIListAttribute bindAddresses = new URIListAttribute(new AttributeMetadata().labelText("bindaddresses"));
    public final StringAttribute contentPath = new StringAttribute(new AttributeMetadata().labelText("context path")).defaultValue("/applicationServer/*");
    public final ObjectValueAttribute<ApplicationServer<L,V,T>> applicationServer = new ObjectValueAttribute<>(new AttributeMetadata().labelText("application server"));

    @Override
    public LiveCycleController<ApplicationServerRestServer, Void> createLifecycleController() {
        return new LiveCycleController<ApplicationServerRestServer, Void>() {
            @Override
            public ApplicationServerRestServer create() {
                ApplicationServer<L,V,T> applicationServer = ApplicationServerRestServerFactory.this.applicationServer.get();
                ApplicationServerResource<L,V,T> applicationServerResource = new ApplicationServerResource<>(applicationServer);
                List<Function<Server,ServerConnector>> connectorFactories = bindAddresses.get().stream().map(a->{
                    String host = a.getHost();
                    int port = a.getPort();
                    boolean isSsl = "https".equals(a.getScheme());
                    if (!isSsl && !"http".equals(a.getScheme()))
                        throw new IllegalArgumentException("Only http and https allowed");
                    return (Function<Server,ServerConnector>)srv->{
                        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(srv);
                        connector.setPort(port);
                        connector.setReuseAddress(true);
                        connector.setHost(Optional.ofNullable(host).map(h->"".equals(h)?"0.0.0.0":h).orElse("0.0.0.0"));
                        return connector;
                    };
                }).collect(Collectors.toList());
                return new ApplicationServerRestServer(applicationServerResource, server->connectorFactories.stream().map(f->f.apply(server)).collect(Collectors.toList()),contentPath.get());
            }

            @Override
            public void start(ApplicationServerRestServer newLiveObject) {
                newLiveObject.start();
            }

            @Override
            public void destroy(ApplicationServerRestServer previousLiveObject) {
                previousLiveObject.stop();
            };
        };
    }
}