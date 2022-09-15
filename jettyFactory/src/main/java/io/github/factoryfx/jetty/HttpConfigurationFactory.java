package io.github.factoryfx.jetty;

import org.eclipse.jetty.server.HttpConfiguration;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;

public class HttpConfigurationFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HttpConfiguration,R> {
    public final BooleanAttribute sendServerVersion = new BooleanAttribute().de("sendServerVersion").en("sendServerVersion");

    @Override
    protected HttpConfiguration createImpl() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(sendServerVersion.get());
        return httpConfiguration;
    }

}
