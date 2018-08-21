package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.*;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.microservice.rest.MicroserviceResourceFactory;

public class ServerBuilder {

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder<ServerRootFactory> builder(){
        FactoryTreeBuilder<ServerRootFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(ServerRootFactory.class);

        factoryTreeBuilder.addFactory(ServerRootFactory.class, Scope.SINGLETON);

        factoryTreeBuilder.addFactory(JettyServerFactory.class, Scope.SINGLETON,  context -> {
            ShopJettyServerFactory jettyServerFactory = new ShopJettyServerFactory();
            jettyServerFactory.resource.set(context.get(MicroserviceResourceFactory.class));
            jettyServerFactory.shopResource.set(context.get(ShopResourceFactory.class));
            HttpServerConnectorFactory<OrderCollector,ServerRootFactory> connector = new HttpServerConnectorFactory<>();
            connector.host.set("localhost");
            connector.port.set(8089);
            jettyServerFactory.connectors.add(connector);
            return jettyServerFactory;
        });

        factoryTreeBuilder.addFactory(MicroserviceResourceFactory.class, Scope.SINGLETON, context -> {
            MicroserviceResourceFactory<OrderCollector,ServerRootFactory,Void> microserviceResourceFactory = new MicroserviceResourceFactory<>();
            return microserviceResourceFactory;
        });

        factoryTreeBuilder.addFactory(ShopResourceFactory.class, Scope.SINGLETON, context -> {
            ShopResourceFactory shopResource = new ShopResourceFactory();
            shopResource.orderStorage.set(context.get(OrderStorageFactory.class));
            shopResource.products.add(context.get(ProductFactory.class,"car"));
            shopResource.products.add(context.get(ProductFactory.class,"bike"));
            return shopResource;
        });

        factoryTreeBuilder.addFactory(ProductFactory.class, "car", Scope.PROTOTYPE, context -> {
            ProductFactory carFactory = new ProductFactory();
            carFactory.vatRate.set(context.get(VatRateFactory.class));
            carFactory.name.set("Car");
            carFactory.price.set(5);
            return carFactory;
        });

        factoryTreeBuilder.addFactory(ProductFactory.class, "bike", Scope.PROTOTYPE, context -> {
            ProductFactory bikeFactory = new ProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Bike");
            bikeFactory.price.set(10);
            return bikeFactory;
        });

        factoryTreeBuilder.addFactory(VatRateFactory.class, Scope.SINGLETON, context -> {
            VatRateFactory vatRate =new VatRateFactory();
            vatRate.rate.set(0.19);
            return vatRate;
        });

        factoryTreeBuilder.addFactory(OrderStorageFactory.class, Scope.SINGLETON, ctx->{
            return new OrderStorageFactory();
        });

        return factoryTreeBuilder;
    }
}
