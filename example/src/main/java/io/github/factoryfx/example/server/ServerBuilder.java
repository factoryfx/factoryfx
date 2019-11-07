package io.github.factoryfx.example.server;

import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.example.server.shop.*;
import io.github.factoryfx.example.server.shop.netherlands.NetherlandsCarProductFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import org.eclipse.jetty.server.Server;

public class ServerBuilder {

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder<Server, JettyServerRootFactory> builder(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                    .withHost("localhost").withPort(8089)
                    .withResource(ctx.getUnsafe(MicroserviceDomResourceFactory.class))
                    .withResource(ctx.get(OrderMonitoringResourceFactory.class))
                    .withResource(ctx.get(ShopResourceFactory.class))
        );

        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON);

        builder.addFactory(ShopResourceFactory.class, Scope.SINGLETON, context -> {
            ShopResourceFactory shopResource = new ShopResourceFactory();
            shopResource.orderStorage.set(context.get(OrderStorageFactory.class));
            shopResource.products.add(context.get(ProductFactory.class,"car"));
            shopResource.products.add(context.get(ProductFactory.class,"bike"));
            shopResource.products.add(context.get(NetherlandsCarProductFactory.class,"netherland car"));
            return shopResource;
        });

        builder.addFactory(ProductFactory.class, "car", Scope.PROTOTYPE, context -> {
            ProductFactory carFactory = new ProductFactory();
            carFactory.vatRate.set(context.get(VatRateFactory.class));
            carFactory.name.set("Car");
            carFactory.price.set(5);
            return carFactory;
        });

        builder.addFactory(ProductFactory.class, "bike", Scope.PROTOTYPE, context -> {
            ProductFactory bikeFactory = new ProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Bike");
            bikeFactory.price.set(10);
            return bikeFactory;
        });

        builder.addFactory(NetherlandsCarProductFactory.class, "netherland car", Scope.PROTOTYPE, context -> {
            NetherlandsCarProductFactory bikeFactory = new NetherlandsCarProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Netherland ");
            bikeFactory.price.set(10);
            bikeFactory.bpmTax.set(0.1);
            return bikeFactory;
        });


        builder.addFactory(VatRateFactory.class, Scope.SINGLETON, context -> {
            VatRateFactory vatRate =new VatRateFactory();
            vatRate.rate.set(0.19);
            return vatRate;
        });

        builder.addFactory(OrderStorageFactory.class, Scope.SINGLETON, ctx->{
            return new OrderStorageFactory();
        });

        builder.addFactory(OrderMonitoringResourceFactory.class, Scope.SINGLETON);

        return builder;
    }
}
