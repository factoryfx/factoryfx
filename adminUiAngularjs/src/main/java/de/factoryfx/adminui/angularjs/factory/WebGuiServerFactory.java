package de.factoryfx.adminui.angularjs.factory;

import java.util.Optional;

import de.factoryfx.adminui.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.util.IntegerAttribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;

public class WebGuiServerFactory<V> extends FactoryBase<WebGuiServer<V>,WebGuiServerFactory<V>> {

    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("port"));
    public final IntegerAttribute sessionTimeoutS=new IntegerAttribute(new AttributeMetadata().labelText("sessionTimeout").addonText("s"));
    public final ReferenceAttribute<WebGuiResource,WebGuiResourceFactory<V>> webGuiResource=new ReferenceAttribute<>(new AttributeMetadata().labelText("WebGuiResource"),WebGuiResourceFactory.class);
    public final ObjectValueAttribute<ConfigurableResourceHandler> resourceHandler=new ObjectValueAttribute<>(new AttributeMetadata().labelText("resourceHandler"));

    @Override
    protected WebGuiServer<V> createImp(Optional<WebGuiServer<V>> previousLiveObject) {
        return new WebGuiServer<>(port.get(),host.get(),sessionTimeoutS.get(),webGuiResource.get().instance(),resourceHandler.get());
    }
}
