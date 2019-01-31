package de.factoryfx.docu.restserver;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class WebResourceFactory extends SimpleFactoryBase<Object, Void, SimpleHttpServer> {

    public final StringAttribute responseText = new StringAttribute().labelText("Text");

    @Override
    public Object createImpl() {
        return new WebResource(responseText.get());
    }
}
