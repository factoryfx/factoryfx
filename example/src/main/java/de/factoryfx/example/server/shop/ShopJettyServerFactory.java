package de.factoryfx.example.server.shop;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.microservice.rest.MicroserviceResource;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;


import java.util.List;

public class ShopJettyServerFactory extends JettyServerFactory<OrderCollector,ServerRootFactory> {
    public final FactoryReferenceAttribute<MicroserviceResource<OrderCollector, ServerRootFactory,Void>, MicroserviceResourceFactory<OrderCollector, ServerRootFactory,Void>> resource = new FactoryReferenceAttribute<MicroserviceResource<OrderCollector, ServerRootFactory,Void>, MicroserviceResourceFactory<OrderCollector, ServerRootFactory,Void>>().setupUnsafe(MicroserviceResourceFactory.class);
    public final FactoryReferenceAttribute<ShopResource, ShopResourceFactory> shopResource = new FactoryReferenceAttribute<ShopResource, ShopResourceFactory>().setupUnsafe(ShopResourceFactory.class);


    @Override
    protected List<Object> getResourcesInstances() {
        return List.of(resource.instance(),shopResource.instance());
    }
}
