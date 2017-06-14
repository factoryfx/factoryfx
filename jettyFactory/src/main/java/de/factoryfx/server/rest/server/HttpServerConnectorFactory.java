package de.factoryfx.server.rest.server;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class HttpServerConnectorFactory<V> extends SimpleFactoryBase<HttpServerConnectorCreator,V> {
    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");

    @Override
    public HttpServerConnectorCreator createImpl() {
        return new HttpServerConnectorCreator(host.get(),port.get());
    }
    public HttpServerConnectorFactory(){
        config().setDisplayTextProvider(() -> "http://"+host.get()+":"+port.get());
    }
}
