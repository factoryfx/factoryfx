package de.factoryfx.docu.dynamicwebserver;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class WebResourceFactory extends SimpleFactoryBase<WebResource,Void> {

    public final StringAttribute responseText = new StringAttribute(new AttributeMetadata().labelText("Text"));

    @Override
    public WebResource createImpl() {
        return new WebResource(responseText.get());
    }

}
