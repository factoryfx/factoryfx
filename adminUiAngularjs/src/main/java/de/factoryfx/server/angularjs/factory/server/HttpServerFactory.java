package de.factoryfx.server.angularjs.factory.server;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.angularjs.factory.RestResource;
import de.factoryfx.server.angularjs.factory.RestResourceFactory;
import de.factoryfx.server.angularjs.factory.server.resourcehandler.ConfigurableResourceHandler;

public class HttpServerFactory<V,L, R extends FactoryBase<L,V>> extends FactoryBase<HttpServer,Void> {

    public final StringAttribute host=new StringAttribute(new AttributeMetadata().labelText("host"));
    public final IntegerAttribute port=new IntegerAttribute(new AttributeMetadata().labelText("port"));
    public final IntegerAttribute sessionTimeoutS=new IntegerAttribute(new AttributeMetadata().labelText("sessionTimeout").addonText("s"));
    public final FactoryReferenceAttribute<RestResource,RestResourceFactory<V,L, R>> webGuiResource=new FactoryReferenceAttribute<>(new AttributeMetadata().labelText("RestResource"),RestResourceFactory.class);
    public final ObjectValueAttribute<ConfigurableResourceHandler> resourceHandler=new ObjectValueAttribute<>(new AttributeMetadata().labelText("resourceHandler"));

    public HttpServerFactory(){
        configLiveCycle().setCreator(() -> new HttpServer(port.get(),host.get(),sessionTimeoutS.get(),webGuiResource.instance(),resourceHandler.get()));

        configLiveCycle().setStarter(newLiveObject -> newLiveObject.start());
        configLiveCycle().setDestroyer(previousLiveObject -> previousLiveObject.stop());
    }

}
