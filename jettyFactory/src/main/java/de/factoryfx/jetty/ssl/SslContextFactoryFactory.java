package de.factoryfx.jetty.ssl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import org.eclipse.jetty.util.ssl.SslContextFactory;

import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.types.Base64Attribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class SslContextFactoryFactory<V, R extends FactoryBase<?, V, R>> extends SimpleFactoryBase<SslContextFactory, V, R> {
    public final Base64Attribute keyStore = new Base64Attribute().en("keyStore").de("keyStore");
    public final EnumAttribute<KeyStoreType> keyStoreType = new EnumAttribute<>(KeyStoreType.class).en("keyStoreType").de("keyStoreType");
    public final StringAttribute keyStorePassword = new StringAttribute().en("keyStorePassword").de("keyStorePassword");
    public final StringAttribute keyPassword = new StringAttribute().en("keyPassword").de("keyPassword").nullable();

    public final Base64Attribute trustStore = new Base64Attribute().en("trustStore").de("trustStore");
    public final EnumAttribute<KeyStoreType> trustStoreType = new EnumAttribute<>(KeyStoreType.class).en("trustStoreType").de("trustStoreType");
    public final StringAttribute trustStorePassword = new StringAttribute().en("trustStorePassword").de("trustStorePassword");

    public final StringAttribute certAlias = new StringAttribute().en("certAlias").de("certAlias").nullable();

    public final StringListAttribute allowCipherSuites = new StringListAttribute().en("Cipher suites to allow");

    public final BooleanAttribute wantClientAuth = new BooleanAttribute().en("wantClientAuth").de("wantClientAuth").defaultValue(false);
    public final BooleanAttribute needClientAuth = new BooleanAttribute().en("needClientAuth").de("needClientAuth").defaultValue(false);

    @Override
    public SslContextFactory createImpl() {
        SslContextFactory sslContextFactory = new SslContextFactory();

        if (allowCipherSuites.size() > 0) {
            sslContextFactory.setExcludeCipherSuites();
            sslContextFactory.setIncludeCipherSuites(allowCipherSuites.get().toArray(new String[allowCipherSuites.size()]));
        }

        sslContextFactory.setKeyStorePassword(keyStorePassword.get());
        if (!keyPassword.isEmpty()) {
            sslContextFactory.setKeyManagerPassword(keyPassword.get());
        }

        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType.get().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.keyStore.getBytes())) {
                keyStore.load(inputStream, keyStorePassword.get().toCharArray());
                sslContextFactory.setKeyStore(keyStore);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sslContextFactory.setNeedClientAuth(needClientAuth.get());
        sslContextFactory.setWantClientAuth(wantClientAuth.get());
        sslContextFactory.setKeyStorePassword(trustStorePassword.get());
        try {
            KeyStore keyStore = KeyStore.getInstance(trustStoreType.get().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.trustStore.getBytes())) {
                keyStore.load(inputStream, trustStorePassword.get().toCharArray());
                sslContextFactory.setTrustStore(keyStore);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sslContextFactory.setCertAlias(certAlias.get());

        return sslContextFactory;
    }
}
