package de.factoryfx.docu.dynamicwebserver;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.docu.helloworld.HelloWorld;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class WebResourceFactory extends SimpleFactoryBase<WebResource,Void> {

    public final StringAttribute responseText = new StringAttribute(new AttributeMetadata().labelText("Text"));

    @Override
    public WebResource createImpl() {
        return new WebResource(responseText.get());
    }

}
