package de.factoryfx.example.server.shop;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.jetty.ServletBuilder;
import de.factoryfx.microservice.rest.MicroserviceResource;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;


import java.util.List;

public class ShopJettyServerFactory extends JettyServerFactory<OrderCollector,ServerRootFactory> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MicroserviceResource<OrderCollector, ServerRootFactory,Void>, MicroserviceResourceFactory<OrderCollector, ServerRootFactory,Void>> resource = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceResourceFactory.class).labelText("Configuration API"));
    public final FactoryReferenceAttribute<ShopResource, ShopResourceFactory> shopResource = new FactoryReferenceAttribute<>(ShopResourceFactory.class).labelText("Shop API");

    @Override
    protected void setupServlets(ServletBuilder servletBuilder) {
        defaultSetupServlets(servletBuilder,resource.instance(),shopResource.instance());
    }
}
