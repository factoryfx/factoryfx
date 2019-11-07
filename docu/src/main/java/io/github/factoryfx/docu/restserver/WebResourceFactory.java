package io.github.factoryfx.docu.restserver;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class WebResourceFactory extends SimpleFactoryBase<Object, JettyServerRootFactory> {

    public final StringAttribute responseText = new StringAttribute().labelText("Text");

    @Override
    protected Object createImpl() {
        return new WebResource(responseText.get());
    }
}
