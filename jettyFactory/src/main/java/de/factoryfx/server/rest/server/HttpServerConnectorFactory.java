package de.factoryfx.server.rest.server;

import java.util.function.Function;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class HttpServerConnectorFactory<V> extends SimpleFactoryBase<HttpServerConnectorCreator,V> {
    public final StringAttribute host = new StringAttribute(new AttributeMetadata().de("host").en("host"));
    public final IntegerAttribute port = new IntegerAttribute(new AttributeMetadata().de("port").en("port"));

    @Override
    public HttpServerConnectorCreator createImpl() {
        return new HttpServerConnectorCreator(host.get(),port.get());
    }
    public HttpServerConnectorFactory(){
        config().setDisplayTextProvider(() -> "http://"+host.get()+":"+port.get());
    }
}
