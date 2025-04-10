package io.github.factoryfx.jetty.ssl;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.ini4j.Ini;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Factory to create org.eclipse.jetty.util.ssl.SslContextFactory based on an INI file.
 *
 * Attributes are the path to the ini file on the server's filesystem, and the name of the ini file section.
 *
 * <pre>
 * [SectionName]
 *
 * KEYSTORE = path/to/keystore
 * KEYSTORE_PASSWORD = password
 * KEYSTORE_TYPE = pkcs12
 *
 * TRUSTSTORE = path/to/keystore
 * TRUSTSTORE_PASSWORD = password
 * TRUSTSTORE_TYPE = pkcs12
 * CERT_ALIAS = lotus
 *
 * #Optional fields
 * ALLOWED_CIPHER_SUITES = TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA
 * KEY_PASSWORD = key_password
 *
 * #Specific to io.github.factoryfx.jetty.ssl.ServerIniFileSslContextFactoryFactory
 * NEED_CLIENT_AUTH = true
 * WANT_CLIENT_AUTH = false
 *
 * </pre>
 *
 * KEYSTORE and TRUSTSTORE are either absolute paths, or paths relative to the ini file
 *
 *
 */
public abstract class AbstractIniFileSslContextFactoryFactory<L extends SslContextFactory, R extends FactoryBase<?, R>> extends SimpleFactoryBase<L, R> {

    public final StringAttribute iniFile = new StringAttribute().en("INI file path").de("INI file path");
    public final StringAttribute section = new StringAttribute().en("Section").de("Section");

    abstract AbstractSslContextFactoryFactory<L, R> createFactoryFactory();

    public AbstractIniFileSslContextFactoryFactory() {
        config().setDisplayTextProvider(() -> getClass().getSimpleName() + " (path: " + iniFile.get() + ", section: " + section.get() + ")",
                iniFile,
                section);
    }

    protected Ini loadIniFile() throws IOException {

        Ini ini = new Ini();
        ini.getConfig().setFileEncoding(StandardCharsets.UTF_8);
        ini.load(Paths.get(iniFile.get()).toFile());
        return ini;
    }

    @Override
    protected L createImpl() {
        AbstractSslContextFactoryFactory<L, R> factoryFactory = createFactoryFactory();

        try {
            Ini ini = loadIniFile();

            Path iniFileDir = Paths.get(iniFile.get()).getParent().toAbsolutePath();
            factoryFactory.keyStore.set(Files.readAllBytes(iniFileDir.resolve(ini.get(section.get(), "KEYSTORE"))));
            factoryFactory.keyStoreType.set(KeyStoreType.valueOf(ini.get(section.get(), "KEYSTORE_TYPE")));
            factoryFactory.keyStorePassword.set(ini.get(section.get(), "KEYSTORE_PASSWORD"));
            factoryFactory.keyPassword.set(ini.get(section.get(), "KEY_PASSWORD"));

            factoryFactory.trustStore.set(Files.readAllBytes(iniFileDir.resolve(ini.get(section.get(), "TRUSTSTORE"))));
            factoryFactory.trustStoreType.set(KeyStoreType.valueOf(ini.get(section.get(), "TRUSTSTORE_TYPE")));
            factoryFactory.trustStorePassword.set(ini.get(section.get(), "TRUSTSTORE_PASSWORD"));

            factoryFactory.certAlias.set(ini.get(section.get(), "CERT_ALIAS"));

            factoryFactory.allowCipherSuites.addAll(Optional.ofNullable(ini.get(section.get(), "ALLOWED_CIPHER_SUITES")).map(s -> Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList())).orElse(Collections.emptyList()));
            return factoryFactory.createImpl();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
