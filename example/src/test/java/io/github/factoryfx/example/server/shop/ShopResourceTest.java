package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBuilder;
import io.github.factoryfx.example.server.testutils.FactoryTreeBuilderRule;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ShopResourceTest {

    private ShopResource shopResource;
    private OrderStorage orderStorage;

    @RegisterExtension
    public final FactoryTreeBuilderRule<Server, JettyServerRootFactory> ctx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder(), rule -> {
            shopResource = rule.get(ShopResourceFactory.class);
            orderStorage = rule.get(OrderStorageFactory.class);
    });

    @Test
    public void testShopResource() {

        ShopResource.BuyProductRequest buyProductRequest = new ShopResource.BuyProductRequest();
        buyProductRequest.productName = "Car";
        buyProductRequest.userName = "testuser";

        shopResource.buyProducts(buyProductRequest);

        OrderStorage.Order order = orderStorage.getOrders().stream()
                .filter(o -> o.customerName.equals("testuser"))
                .findFirst()
                .get();

        Assertions.assertEquals("Car",order.productName,"product should be 'Car'");
    }
}
