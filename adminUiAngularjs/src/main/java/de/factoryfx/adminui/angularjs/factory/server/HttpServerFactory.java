package de.factoryfx.adminui.angularjs.factory.server;

import java.util.Optional;

import de.factoryfx.adminui.angularjs.factory.RestResource;
import de.factoryfx.adminui.angularjs.factory.RestResourceFactory;
import de.factoryfx.adminui.angularjs.factory.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class HttpServerFactory<L,V,T extends FactoryBase<L,V>> extends FactoryBase<HttpServer,Void> {

    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("port"));
    public final IntegerAttribute sessionTimeoutS=new IntegerAttribute(new AttributeMetadata().labelText("sessionTimeout").addonText("s"));
    public final FactoryReferenceAttribute<RestResource,RestResourceFactory<L,V,T>> webGuiResource=new FactoryReferenceAttribute<>(new AttributeMetadata().labelText("RestResource"),RestResourceFactory.class);
    public final ObjectValueAttribute<ConfigurableResourceHandler> resourceHandler=new ObjectValueAttribute<>(new AttributeMetadata().labelText("resourceHandler"));

    @Override
    protected HttpServer createImp(Optional<HttpServer> previousLiveObject, LifecycleNotifier<Void> lifecycle) {
        return new HttpServer(port.get(),host.get(),sessionTimeoutS.get(),webGuiResource.get().instance(),resourceHandler.get(),lifecycle);
    }

}
