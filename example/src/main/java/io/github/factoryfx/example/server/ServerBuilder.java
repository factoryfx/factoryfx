package io.github.factoryfx.example.server;

import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.example.server.shop.*;
import io.github.factoryfx.example.server.shop.netherlands.NetherlandsCarProductFactory;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import org.eclipse.jetty.server.Server;

import java.time.Duration;

public class ServerBuilder {

    public static FactoryTreeBuilder<Server, JettyServerRootFactory> build(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                    .withHost("localhost").withPort(8089)
                    .withResource(new FactoryTemplateId<>(MicroserviceDomResourceFactory.class))
                    .withResource(new FactoryTemplateId<>(OrderMonitoringResourceFactory.class))
                    .withResource(new FactoryTemplateId<>(ShopResourceFactory.class))
        );

        builder.addFactoryUnsafe(MicroserviceDomResourceFactory.class, Scope.SINGLETON);

        builder.addSingleton(ShopResourceFactory.class, context -> {
            ShopResourceFactory shopResource = new ShopResourceFactory();
            shopResource.orderStorage.set(context.get(OrderStorageFactory.class));
            shopResource.products.add(context.get(ProductFactory.class,"car"));
            shopResource.products.add(context.get(ProductFactory.class,"bike"));
            shopResource.products.add(context.get(NetherlandsCarProductFactory.class,"netherland car"));
            return shopResource;
        });

        builder.addPrototype(ProductFactory.class, "car", context -> {
            ProductFactory carFactory = new ProductFactory();
            carFactory.vatRate.set(context.get(VatRateFactory.class));
            carFactory.name.set("Car");
            carFactory.price.set(5);
            return carFactory;
        });

        builder.addPrototype(ProductFactory.class, "bike", context -> {
            ProductFactory bikeFactory = new ProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Bike");
            bikeFactory.price.set(10);
            return bikeFactory;
        });

        builder.addPrototype(NetherlandsCarProductFactory.class, "netherland car", context -> {
            NetherlandsCarProductFactory bikeFactory = new NetherlandsCarProductFactory();
            bikeFactory.vatRate.set(context.get(VatRateFactory.class));
            bikeFactory.name.set("Netherland ");
            bikeFactory.price.set(10);
            bikeFactory.bpmTax.set(0.1);
            return bikeFactory;
        });


        builder.addSingleton(VatRateFactory.class, context -> {
            VatRateFactory vatRate =new VatRateFactory();
            vatRate.rate.set(0.19);
            return vatRate;
        });

        builder.addSingleton(OrderStorageFactory.class);

        builder.addSingleton(OrderMonitoringResourceFactory.class,ctx->{
            OrderMonitoringResourceFactory factory = new OrderMonitoringResourceFactory();
            factory.attribute.set(Duration.ofDays(1));
            factory.orderStorage.set(ctx.get(OrderStorageFactory.class));
            return factory;

        });
        return builder;
    }
}
