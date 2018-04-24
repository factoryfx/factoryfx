package de.factoryfx.example.factory;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.MicroserviceResource;
import de.factoryfx.server.rest.MicroserviceResourceFactory;
import de.factoryfx.server.rest.server.JettyServerFactory;

import java.util.Collections;
import java.util.List;

public class ShopJettyServerFactory extends JettyServerFactory<OrderCollector,ShopFactory> {
    public final FactoryReferenceAttribute<MicroserviceResource<OrderCollector, ShopFactory,Void>, MicroserviceResourceFactory<OrderCollector, ShopFactory,Void>> resource = new FactoryReferenceAttribute<MicroserviceResource<OrderCollector, ShopFactory,Void>, MicroserviceResourceFactory<OrderCollector, ShopFactory,Void>>().setupUnsafe(MicroserviceResourceFactory.class);

    @Override
    protected List<Object> getResourcesInstances() {
        return Collections.singletonList(resource.instance());
    }
}
