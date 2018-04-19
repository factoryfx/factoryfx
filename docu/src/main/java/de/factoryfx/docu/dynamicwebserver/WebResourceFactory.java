package de.factoryfx.docu.dynamicwebserver;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class WebResourceFactory extends SimpleFactoryBase<WebResource,Void,RootFactory> {

    public final StringAttribute responseText = new StringAttribute().labelText("Text");

    @Override
    public WebResource createImpl() {
        return new WebResource(responseText.get());
    }

}
