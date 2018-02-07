package de.factoryfx.server.rest.server;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.server.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnectorFactory<V> extends SimpleFactoryBase<HttpServerConnectorCreator,V> {
    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");
    public final FactoryReferenceAttribute<SslContextFactory,SslContextFactoryFactory<V>> ssl = new FactoryReferenceAttribute<SslContextFactory,SslContextFactoryFactory<V>>().setupUnsafe(SslContextFactoryFactory.class).de("ssl").en("ssl");

    @Override
    public HttpServerConnectorCreator createImpl() {
        return new HttpServerConnectorCreator(host.get(),port.get(),ssl.instance());
    }
    public HttpServerConnectorFactory(){
        config().setDisplayTextProvider(() -> {
            String protocol="http";
            if (ssl.get()!=null){
                protocol="https";
            }
            return protocol+"://"+host.get()+":"+port.get();
        });
    }
}
