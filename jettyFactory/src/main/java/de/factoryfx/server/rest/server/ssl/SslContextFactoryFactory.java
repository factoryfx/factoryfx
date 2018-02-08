package de.factoryfx.server.rest.server.ssl;

import de.factoryfx.data.attribute.types.Base64Attribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class SslContextFactoryFactory<V> extends SimpleFactoryBase<SslContextFactory,V> {
    public final Base64Attribute keyStore=new Base64Attribute().validation(new ObjectRequired<>()).en("keyStore").de("keyStore");
    public final EnumAttribute<KeyStoreType> keyStoreType=new EnumAttribute<>(KeyStoreType.class).validation(new ObjectRequired<>());
    public final StringAttribute keyStorePassword = new StringAttribute().en("keyStorePassword").de("keyStorePassword").validation(new StringRequired());

    public final Base64Attribute trustStore = new Base64Attribute().en("trustStore").de("trustStore").validation(new ObjectRequired<>());;
    public final EnumAttribute<KeyStoreType> trustStoreType=new EnumAttribute<>(KeyStoreType.class).validation(new ObjectRequired<>());
    public final StringAttribute trustStorePassword = new StringAttribute().en("trustStorePassword").de("trustStorePassword").validation(new StringRequired());;

    public final StringAttribute certAlias = new StringAttribute().en("certAlias").de("certAlias");

    @Override
    public SslContextFactory createImpl() {
        SslContextFactory sslContextFactory = new SslContextFactory();

        sslContextFactory.setKeyStorePassword(keyStorePassword.get());
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType.getEnum().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.keyStore.getBytes())) {
                keyStore.load(inputStream, keyStorePassword.get().toCharArray());
                sslContextFactory.setKeyStore(keyStore);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sslContextFactory.setKeyStorePassword(trustStorePassword.get());
        try {
            KeyStore keyStore = KeyStore.getInstance(trustStoreType.getEnum().value());
            try (InputStream inputStream = new ByteArrayInputStream(this.trustStore.getBytes())) {
                keyStore.load(inputStream, keyStorePassword.get().toCharArray());
                sslContextFactory.setTrustStore(keyStore);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sslContextFactory.setCertAlias(certAlias.get());

        return sslContextFactory;
    }

}
