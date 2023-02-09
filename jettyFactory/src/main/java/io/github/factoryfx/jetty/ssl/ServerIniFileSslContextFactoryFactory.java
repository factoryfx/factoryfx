package io.github.factoryfx.jetty.ssl;

import io.github.factoryfx.factory.FactoryBase;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.ini4j.Ini;

import java.io.IOException;
import java.util.Optional;

public class ServerIniFileSslContextFactoryFactory<R extends FactoryBase<?, R>> extends AbstractIniFileSslContextFactoryFactory<SslContextFactory.Server, R> {

    @Override
    AbstractSslContextFactoryFactory<SslContextFactory.Server, R> createFactoryFactory() {
        ServerSslContextFactoryFactory<R> factory = new ServerSslContextFactoryFactory<>();
        Ini ini = null;
        try {
            ini = loadIniFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        factory.needClientAuth.set(Optional.ofNullable(ini.get(section.get(), "NEED_CLIENT_AUTH")).map(Boolean::valueOf).orElse(false));
        factory.wantClientAuth.set(Optional.ofNullable(ini.get(section.get(), "WANT_CLIENT_AUTH")).map(Boolean::valueOf).orElse(false));
        return factory;
    }

}
