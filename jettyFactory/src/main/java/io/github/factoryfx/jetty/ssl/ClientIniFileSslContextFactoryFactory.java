package io.github.factoryfx.jetty.ssl;

import io.github.factoryfx.factory.FactoryBase;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ClientIniFileSslContextFactoryFactory<R extends FactoryBase<?, R>> extends AbstractIniFileSslContextFactoryFactory<SslContextFactory.Client, R> {

    @Override
    AbstractSslContextFactoryFactory<SslContextFactory.Client, R> createFactoryFactory() {
        return new ClientSslContextFactoryFactory<>();
    }
}
