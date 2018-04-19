package de.factoryfx.example.factory;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.ApplicationServerResource;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;
import de.factoryfx.server.rest.server.JettyServerFactory;

import java.util.Collections;
import java.util.List;

public class ShopJettyServerFactory extends JettyServerFactory<OrderCollector,ShopFactory> {
    public final FactoryReferenceAttribute<ApplicationServerResource<OrderCollector, ShopFactory,Void>, ApplicationServerResourceFactory<OrderCollector, ShopFactory,Void>> resource = new FactoryReferenceAttribute<ApplicationServerResource<OrderCollector, ShopFactory,Void>, ApplicationServerResourceFactory<OrderCollector, ShopFactory,Void>>().setupUnsafe(ApplicationServerResourceFactory.class);

    @Override
    protected List<Object> getResourcesInstances() {
        return Collections.singletonList(resource.instance());
    }
}
