package io.github.factoryfx.docu.restserver;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class WebResourceFactory extends SimpleFactoryBase<Object, SimpleHttpServer> {

    public final StringAttribute responseText = new StringAttribute().labelText("Text");

    @Override
    protected Object createImpl() {
        return new WebResource(responseText.get());
    }
}
