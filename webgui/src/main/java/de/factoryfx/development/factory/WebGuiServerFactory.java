package de.factoryfx.development.factory;

import java.util.Optional;

import de.factoryfx.development.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.util.IntegerAttribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;

public class WebGuiServerFactory extends FactoryBase<WebGuiServer,WebGuiServerFactory> {

    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("port"));
    public final ReferenceAttribute<WebGuiResourceFactory> webGuiResource=new ReferenceAttribute<>(WebGuiResourceFactory.class,new AttributeMetadata().labelText("WebGuiResource"));
    public final ObjectValueAttribute<ConfigurableResourceHandler> resourceHandler=new ObjectValueAttribute<>(new AttributeMetadata().labelText("resourceHandler"));

    @Override
    protected WebGuiServer createImp(Optional<WebGuiServer> previousLiveObject) {
        return new WebGuiServer(port.get(),host.get(),webGuiResource.get().create(),resourceHandler.get());
    }
}
