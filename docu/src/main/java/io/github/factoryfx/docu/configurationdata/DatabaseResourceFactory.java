package io.github.factoryfx.docu.configurationdata;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class DatabaseResourceFactory  extends SimpleFactoryBase<DatabaseResource, JettyServerRootFactory> {
    public final StringAttribute url = new StringAttribute();
    public final StringAttribute user = new StringAttribute();
    public final StringAttribute password = new StringAttribute();

    @Override
    protected DatabaseResource createImpl() {
        return new DatabaseResource(url.get(),user.get(),password.get());
    }
}
