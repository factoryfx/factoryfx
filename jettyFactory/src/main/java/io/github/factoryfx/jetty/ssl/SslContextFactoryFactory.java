package io.github.factoryfx.jetty.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.FileContentAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.StringListAttribute;

public abstract class SslContextFactoryFactory<L extends SslContextFactory, R extends FactoryBase<?, R>> extends SimpleFactoryBase<L, R> {
    public final FileContentAttribute keyStore = new FileContentAttribute().en("keyStore").de("keyStore");
    public final EnumAttribute<KeyStoreType> keyStoreType = new EnumAttribute<KeyStoreType>().en("keyStoreType").de("keyStoreType");
    public final StringAttribute keyStorePassword = new StringAttribute().en("keyStorePassword").de("keyStorePassword");
    public final StringAttribute keyPassword = new StringAttribute().en("keyPassword").de("keyPassword").nullable();

    public final FileContentAttribute trustStore = new FileContentAttribute().en("trustStore").de("trustStore");
    public final EnumAttribute<KeyStoreType> trustStoreType = new EnumAttribute<KeyStoreType>().en("trustStoreType").de("trustStoreType");
    public final StringAttribute trustStorePassword = new StringAttribute().en("trustStorePassword").de("trustStorePassword");

    public final StringAttribute certAlias = new StringAttribute().en("certAlias").de("certAlias").nullable();

    public final StringListAttribute allowCipherSuites = new StringListAttribute().en("Cipher suites to allow");

    abstract L createFactory();

    @Override
    protected L createImpl() {
        try {
            L sslContextFactory = createFactory();

            if (allowCipherSuites.size() > 0) {
                sslContextFactory.setExcludeCipherSuites();
                sslContextFactory.setIncludeCipherSuites(allowCipherSuites.get().toArray(new String[allowCipherSuites.size()]));
            }

            sslContextFactory.setKeyStorePassword(keyStorePassword.get());
            if (!keyPassword.isEmpty()) {
                sslContextFactory.setKeyManagerPassword(keyPassword.get());
            }

            KeyStore keyStore = KeyStore.getInstance(keyStoreType.get().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.keyStore.get())) {
                keyStore.load(inputStream, keyStorePassword.get().toCharArray());
                sslContextFactory.setKeyStore(keyStore);
            }

            sslContextFactory.setKeyStorePassword(trustStorePassword.get());

            KeyStore trustStore = KeyStore.getInstance(trustStoreType.get().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.trustStore.get())) {
                trustStore.load(inputStream, trustStorePassword.get().toCharArray());
                sslContextFactory.setTrustStore(trustStore);
            }

            sslContextFactory.setCertAlias(certAlias.get());

            return sslContextFactory;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
