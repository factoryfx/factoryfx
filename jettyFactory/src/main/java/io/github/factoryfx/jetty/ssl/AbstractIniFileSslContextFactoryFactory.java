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

public abstract class AbstractIniFileSslContextFactoryFactory<L extends SslContextFactory, R extends FactoryBase<?, R>> extends SimpleFactoryBase<L, R> {

    public StringAttribute iniFile = new StringAttribute().en("INI file path").de("INI file path");
    public StringAttribute section = new StringAttribute().en("Section").de("Section");

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
