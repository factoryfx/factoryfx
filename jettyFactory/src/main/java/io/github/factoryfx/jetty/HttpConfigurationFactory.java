package io.github.factoryfx.jetty;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;

public class HttpConfigurationFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HttpConfiguration,R> {
    public final BooleanAttribute sendServerVersion = new BooleanAttribute().de("sendServerVersion").en("sendServerVersion");
    public final BooleanAttribute disableSniHostcheck = new BooleanAttribute().de("disable SNI host check").en("disable SNI host check");

    @Override
    protected HttpConfiguration createImpl() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        if(disableSniHostcheck.get()) {
            SecureRequestCustomizer src = new SecureRequestCustomizer();
            src.setSniHostCheck(false);
            httpConfiguration.addCustomizer(src);
        }
        httpConfiguration.setSendServerVersion(sendServerVersion.get());
        return httpConfiguration;
    }

}
