package de.factoryfx.adminui.angularjs.factory.server;

import java.util.Optional;

import de.factoryfx.adminui.angularjs.factory.RestResource;
import de.factoryfx.adminui.angularjs.factory.RestResourceFactory;
import de.factoryfx.adminui.angularjs.factory.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.util.IntegerAttribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;

public class HttpServerFactory<V> extends FactoryBase<HttpServer,HttpServerFactory<V>> {

    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("port"));
    public final IntegerAttribute sessionTimeoutS=new IntegerAttribute(new AttributeMetadata().labelText("sessionTimeout").addonText("s"));
    public final ReferenceAttribute<RestResource,RestResourceFactory<V>> webGuiResource=new ReferenceAttribute<>(new AttributeMetadata().labelText("RestResource"),RestResourceFactory.class);
    public final ObjectValueAttribute<ConfigurableResourceHandler> resourceHandler=new ObjectValueAttribute<>(new AttributeMetadata().labelText("resourceHandler"));

    @Override
    protected HttpServer createImp(Optional<HttpServer> previousLiveObject) {
        return new HttpServer(port.get(),host.get(),sessionTimeoutS.get(),webGuiResource.get().instance(),resourceHandler.get());
    }
}
