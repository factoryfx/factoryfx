package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class WebResourceFactory extends SimpleFactoryBase<Object, SimpleHttpServer> {

    public final StringAttribute responseText = new StringAttribute().labelText("Text");

    @Override
    protected Object createImpl() {
        return new WebResource(responseText.get());
    }
}
