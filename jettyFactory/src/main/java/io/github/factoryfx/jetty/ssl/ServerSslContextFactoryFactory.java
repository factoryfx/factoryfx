package io.github.factoryfx.jetty.ssl;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;

public class ServerSslContextFactoryFactory<R extends FactoryBase<?, R>> extends AbstractSslContextFactoryFactory<SslContextFactory.Server, R> {

    public final BooleanAttribute wantClientAuth = new BooleanAttribute().en("wantClientAuth").de("wantClientAuth").defaultValue(false);
    public final BooleanAttribute needClientAuth = new BooleanAttribute().en("needClientAuth").de("needClientAuth").defaultValue(false);

    @Override
    SslContextFactory.Server createFactory() {
        return new SslContextFactory.Server();
    }

    @Override
    protected SslContextFactory.Server createImpl() {
        SslContextFactory.Server sslContextFactory = super.createImpl();
        sslContextFactory.setNeedClientAuth(needClientAuth.get());
        sslContextFactory.setWantClientAuth(wantClientAuth.get());
        return sslContextFactory;
    }
}
