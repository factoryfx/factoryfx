package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.jetty.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpConfigurationFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HttpConfiguration,R> {
    public final BooleanAttribute sendServerVersion = new BooleanAttribute().de("sendServerVersion").en("sendServerVersion");

    @Override
    protected HttpConfiguration createImpl() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(sendServerVersion.get());
        return httpConfiguration;
    }

}
