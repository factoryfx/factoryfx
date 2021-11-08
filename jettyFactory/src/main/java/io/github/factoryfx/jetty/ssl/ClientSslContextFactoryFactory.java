package io.github.factoryfx.jetty.ssl;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import io.github.factoryfx.factory.FactoryBase;

public class ClientSslContextFactoryFactory<R extends FactoryBase<?, R>> extends SslContextFactoryFactory<SslContextFactory.Client, R> {
    @Override
    SslContextFactory.Client createFactory() {
        return new SslContextFactory.Client();
    }
}
