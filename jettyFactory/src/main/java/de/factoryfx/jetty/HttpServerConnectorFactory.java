package de.factoryfx.jetty;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnectorFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<HttpServerConnectorCreator,V,R> {
    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");
    public final FactoryReferenceAttribute<SslContextFactory,SslContextFactoryFactory<V,R>> ssl = new FactoryReferenceAttribute<SslContextFactory,SslContextFactoryFactory<V,R>>().setupUnsafe(SslContextFactoryFactory.class).de("ssl").en("ssl").nullable();

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
