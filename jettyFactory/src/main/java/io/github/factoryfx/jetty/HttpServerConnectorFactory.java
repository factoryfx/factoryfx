package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnectorFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HttpServerConnector,R> {
    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");

    public final FactoryAttribute<SslContextFactory, SslContextFactoryFactory<R>> ssl = new FactoryAttribute<SslContextFactory, SslContextFactoryFactory<R>>().de("ssl").en("ssl").nullable();
    public final FactoryAttribute<HttpConfiguration, HttpConfigurationFactory<R>> httpConfiguration = new FactoryAttribute<HttpConfiguration, HttpConfigurationFactory<R>>().de("httpConfiguration").en("httpConfiguration").nullable();

    @Override
    protected HttpServerConnector createImpl() {
        return new HttpServerConnector(host.get(),port.get(),ssl.instance(),httpConfiguration.instance());
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
