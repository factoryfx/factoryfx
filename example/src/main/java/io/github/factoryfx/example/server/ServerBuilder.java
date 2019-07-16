package io.github.factoryfx.example.server;

import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.example.server.shop.*;
import io.github.factoryfx.example.server.shop.netherlands.NetherlandsCarProductFactory;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerBuilder {

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder<Server, ServerRootFactory, Void> builder(){
        FactoryTreeBuilder<Server, ServerRootFactory, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ServerRootFactory.class,context -> {
            return new JettyServerBuilder<ServerRootFactory>()
                    .withHost("localhost").withPort(8089)
                    .withResource(context.getUnsafe(MicroserviceDomResourceFactory.class))
                    .withResource(context.get(OrderMonitoringResourceFactory.class))
                    .withResource(context.get(ShopResourceFactory.class)).buildTo(new ServerRootFactory());

        });

        factoryTreeBuilder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON, context -> {
            MicroserviceDomResourceFactory<ServerRootFactory, Void> microserviceDomResourceFactory = new MicroserviceDomResourceFactory<>();
            microserviceDomResourceFactory.factoryTreeBuilderBasedAttributeSetup.set(new FactoryTreeBuilderBasedAttributeSetup<>(factoryTreeBuilder));
            return microserviceDomResourceFactory;
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

        factoryTreeBuilder.addFactory(OrderMonitoringResourceFactory.class, Scope.SINGLETON);

        return factoryTreeBuilder;
    }
}
