package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.*;
import de.factoryfx.example.server.shop.netherlands.NetherlandsCarProductFactory;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Server;

public class ServerBuilder {

    public FactoryTreeBuilder<OrderCollector, Server, ServerRootFactory, Void> builder(){
        FactoryTreeBuilder<OrderCollector, Server, ServerRootFactory, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ServerRootFactory.class);

        factoryTreeBuilder.addFactory(ServerRootFactory.class, Scope.SINGLETON);

        factoryTreeBuilder.addFactory(ShopJettyServerFactory.class, Scope.SINGLETON,  context -> {
            return new JettyServerBuilder<>(new ShopJettyServerFactory())
                    .withHost("localhost").withPort(8089)
                    .withResource(context.get(SpecificMicroserviceResourceFactory.class))
                    .withResource(context.get(ShopResourceFactory.class)).build();

        });

        factoryTreeBuilder.addFactory(SpecificMicroserviceResourceFactory.class, Scope.SINGLETON, context -> {
            return new SpecificMicroserviceResourceFactory();
        });

        factoryTreeBuilder.addFactory(ShopResourceFactory.class, Scope.SINGLETON, context -> {
            ShopResourceFactory shopResource = new ShopResourceFactory();
            shopResource.orderStorage.set(context.get(OrderStorageFactory.class));
            shopResource.products.add(context.get(ProductFactory.class,"car"));
            shopResource.products.add(context.get(ProductFactory.class,"bike"));
            shopResource.products.add(context.get(NetherlandsCarProductFactory.class,"netherland car"));
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

        factoryTreeBuilder.addFactory(NetherlandsCarProductFactory.class, "netherland car", Scope.PROTOTYPE, context -> {
            NetherlandsCarProductFactory bikeFactory = new NetherlandsCarProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Netherland ");
            bikeFactory.price.set(10);
            bikeFactory.bpmTax.set(0.1);
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
