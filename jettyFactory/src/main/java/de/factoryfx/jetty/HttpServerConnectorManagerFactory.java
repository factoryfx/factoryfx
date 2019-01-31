package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class HttpServerConnectorManagerFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<HttpServerConnectorManager,V,R> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<HttpServerConnector,HttpServerConnectorFactory<V,R>> connectors =
            FactoryReferenceListAttribute.create( new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("Connectors").userNotSelectable());

    @Override
    public HttpServerConnectorManager createImpl() {
        return new HttpServerConnectorManager(connectors.instances());
    }

    public HttpServerConnectorManagerFactory(){
        configLifeCycle().setUpdater(httpServerConnectorManager -> httpServerConnectorManager.update(connectors.instances()));
    }
}
